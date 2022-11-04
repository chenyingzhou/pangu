package com.rainbow.pangu.api.service.payment

import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder

interface PaymentExecutor {
    val type: PaymentMethod.Type

    // 申请支付
    fun apply(payParam: PayParam): PaymentOrderUnverifiedVO

    // 短信确认
    fun confirm(paymentOrderNo: String, phone: String, smsCode: String): PaymentOrder.Status
}