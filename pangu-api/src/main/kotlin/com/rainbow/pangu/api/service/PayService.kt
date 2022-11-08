package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.vo.PaymentAccountVO
import com.rainbow.pangu.api.model.vo.PaymentBankVO
import com.rainbow.pangu.api.model.vo.PaymentMethodVO
import com.rainbow.pangu.api.model.vo.converter.PaymentAccountVOConv
import com.rainbow.pangu.api.model.vo.converter.PaymentBankVOConv
import com.rainbow.pangu.api.model.vo.converter.PaymentMethodVOConv
import com.rainbow.pangu.api.service.payment.PaymentExecutor
import com.rainbow.pangu.constant.Platform
import com.rainbow.pangu.entity.PaymentAccount
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.repository.OrderInfoRepo
import com.rainbow.pangu.repository.PaymentAccountRepo
import com.rainbow.pangu.repository.PaymentOrderRepo
import com.rainbow.pangu.util.PaymentUtil
import com.rainbow.pangu.util.SpringContextUtil
import org.springframework.stereotype.Service
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
                    paymentAccountOpt.get().paid = true
                    paymentAccountRepo.save(paymentAccountOpt.get())
                }
            }
            // 更新订单状态(耦合)
            val orderInfoRepo = SpringContextUtil.getBean(OrderInfoRepo::class)
            val orderService = SpringContextUtil.getBean(OrderService::class)
            val orderInfo = orderInfoRepo.findByOrderNo(paymentOrder.orderNo).orElseThrow()
            orderService.paid(orderInfo)
        }
        return success
    }
}