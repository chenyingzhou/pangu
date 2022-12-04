package com.rainbow.pangu.service

import com.rainbow.pangu.constant.Platform
import com.rainbow.pangu.entity.PaymentAccount
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.model.vo.PaymentAccountVO
import com.rainbow.pangu.model.vo.PaymentBankVO
import com.rainbow.pangu.model.vo.PaymentMethodVO
import com.rainbow.pangu.model.vo.converter.PaymentAccountVOConv
import com.rainbow.pangu.model.vo.converter.PaymentBankVOConv
import com.rainbow.pangu.model.vo.converter.PaymentMethodVOConv
import com.rainbow.pangu.repository.*
import com.rainbow.pangu.service.payment.PaymentExecutor
import com.rainbow.pangu.util.AppCtxtUtil
import com.rainbow.pangu.util.PaymentUtil
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.annotation.Resource
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class PayService {
    @Resource
    lateinit var paymentAccountRepo: PaymentAccountRepo

    @Resource
    lateinit var paymentOrderRepo: PaymentOrderRepo

    @Resource
    lateinit var paymentExecutors: List<PaymentExecutor>

    fun methodList(platform: Platform, version: Int): List<PaymentMethodVO> {
        val paymentMethods =
            PaymentUtil.methodList.filter { it.platform == platform && version >= it.versionMin && version <= it.versionMax }
        return PaymentMethodVOConv.fromEntity(paymentMethods)
    }

    fun bankList(type: PaymentMethod.Type): List<PaymentBankVO> {
        val paymentBanks = PaymentUtil.bankList.filter { it.methodType == type }
        return PaymentBankVOConv.fromEntity(paymentBanks)
    }

    fun accountList(userId: Int): List<PaymentAccountVO> {
        val accountList = paymentAccountRepo.findAllByUserId(userId).filter { it.paid }.distinctBy { it.accountNo }
        return PaymentAccountVOConv.fromEntity(accountList)
    }

    fun smsValidate(paymentOrderNo: String, smsCode: String): Boolean {
        val paymentOrder = paymentOrderRepo.findByPaymentOrderNo(paymentOrderNo).orElseThrow()
        if (paymentOrder.createdTime < LocalDateTime.now().minusMinutes(2)) {
            throw BizException("支付超时")
        }
        val phoneNo = paymentAccountRepo.findById(paymentOrder.accountId).orElseGet { PaymentAccount() }.phoneNo
        val paymentExecutor = paymentExecutors.find { it.type == paymentOrder.type }!!
        val status = paymentExecutor.confirm(paymentOrderNo, phoneNo, smsCode)
        // 更新支付订单状态
        paymentOrder.status = status
        paymentOrderRepo.save(paymentOrder)

        val success = status == PaymentOrder.Status.SUCCESS
        if (success) {
            // 将支付账号标记为支付过
            if (paymentOrder.accountId > 0) {
                val paymentAccountOpt = paymentAccountRepo.findById(paymentOrder.accountId)
                if (paymentAccountOpt.isPresent) {
                    val paymentAccount = paymentAccountOpt.get()
                    paymentAccount.paid = true
                    paymentAccountRepo.save(paymentAccount)
                    // 更新用户实名信息(耦合)
                    val user = User.findById(paymentAccount.userId).orElseGet { null }
                    if (user != null && !user.realNameChecked) {
                        user.realName = paymentAccount.accountName
                        user.idCardNo = paymentAccount.idCardNo
                        user.realNameChecked = true
                        user.save()
                    }
                }
            }
            // 更新订单状态(耦合)
            val orderInfoRepo = AppCtxtUtil.getBean(OrderInfoRepo::class)
            val orderService = AppCtxtUtil.getBean(OrderService::class)
            val orderInfoOpt = orderInfoRepo.findByOrderNo(paymentOrder.orderNo)
            if (orderInfoOpt.isPresent) {
                orderService.paid(orderInfoOpt.get())
            }
            // 更新充值状态(耦合)
            val balanceBillRepo = AppCtxtUtil.getBean(BalanceBillRepo::class)
            val balanceService = AppCtxtUtil.getBean(BalanceService::class)
            val balanceBillOpt = balanceBillRepo.findByBillNo(paymentOrder.orderNo)
            if (balanceBillOpt.isPresent) {
                balanceService.check(balanceBillOpt.get())
            }
        }
        return success
    }
}