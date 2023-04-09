package com.rainbow.pangu.service

import com.rainbow.pangu.constant.Platform
import com.rainbow.pangu.entity.*
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.model.vo.PaymentAccountVO
import com.rainbow.pangu.model.vo.PaymentBankVO
import com.rainbow.pangu.model.vo.PaymentMethodVO
import com.rainbow.pangu.model.vo.converter.PaymentAccountVOConv
import com.rainbow.pangu.model.vo.converter.PaymentBankVOConv
import com.rainbow.pangu.model.vo.converter.PaymentMethodVOConv
import com.rainbow.pangu.service.payment.PaymentExecutor
import com.rainbow.pangu.util.BeanUtil
import com.rainbow.pangu.util.PaymentUtil
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import jakarta.annotation.Resource
import jakarta.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class PayService {
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
        val accountList =
            PaymentAccount.findAll(PaymentAccount::userId to userId).filter { it.paid }.distinctBy { it.accountNo }
        return PaymentAccountVOConv.fromEntity(accountList)
    }

    fun smsValidate(paymentOrderNo: String, smsCode: String): Boolean {
        val paymentOrder = PaymentOrder.findOne(PaymentOrder::paymentOrderNo to paymentOrderNo).orElseThrow()
        if (paymentOrder.createdTime < LocalDateTime.now().minusMinutes(2)) {
            throw BizException("支付超时")
        }
        val phoneNo = PaymentAccount.findById(paymentOrder.accountId).orElseGet { PaymentAccount() }.phoneNo
        val paymentExecutor = paymentExecutors.find { it.type == paymentOrder.type }!!
        val status = paymentExecutor.confirm(paymentOrderNo, phoneNo, smsCode)
        // 更新支付订单状态
        paymentOrder.status = status
        paymentOrder.save()

        val success = status == PaymentOrder.Status.SUCCESS
        if (success) {
            // 将支付账号标记为支付过
            if (paymentOrder.accountId > 0) {
                val paymentAccountOpt = PaymentAccount.findById(paymentOrder.accountId)
                if (paymentAccountOpt.isPresent) {
                    val paymentAccount = paymentAccountOpt.get()
                    paymentAccount.paid = true
                    paymentAccount.save()
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
            val orderService = BeanUtil.getBean(OrderService::class)
            val orderInfoOpt = OrderInfo.findOne(OrderInfo::orderNo to paymentOrder.orderNo)
            if (orderInfoOpt.isPresent) {
                orderService.paid(orderInfoOpt.get())
            }
            // 更新充值状态(耦合)
            val balanceService = BeanUtil.getBean(BalanceService::class)
            val balanceBillOpt = BalanceBill.findOne(BalanceBill::billNo to paymentOrder.orderNo)
            if (balanceBillOpt.isPresent) {
                balanceService.check(balanceBillOpt.get())
            }
        }
        return success
    }
}