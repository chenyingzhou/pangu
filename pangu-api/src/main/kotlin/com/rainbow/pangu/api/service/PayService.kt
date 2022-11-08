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
import com.rainbow.pangu.repository.PaymentAccountRepo
import com.rainbow.pangu.repository.PaymentOrderRepo
import com.rainbow.pangu.util.PaymentUtil
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
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
        return status == PaymentOrder.Status.SUCCESS
    }
}