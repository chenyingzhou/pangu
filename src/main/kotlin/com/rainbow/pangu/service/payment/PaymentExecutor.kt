package com.rainbow.pangu.service.payment

import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.model.param.PayParam
import com.rainbow.pangu.model.vo.PaymentOrderUnverifiedVO

interface PaymentExecutor {
    val type: PaymentMethod.Type

    // 申请支付
    fun apply(payParam: PayParam): PaymentOrderUnverifiedVO

    // 短信确认
    fun confirm(paymentOrderNo: String, phone: String, smsCode: String): PaymentOrder.Status

    // 查询状态
    fun queryStatus(paymentOrderNo: String): PaymentOrder.Status
}