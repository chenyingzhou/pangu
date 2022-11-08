package com.rainbow.pangu.api.service.payment

import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import org.springframework.stereotype.Service

@Service
class AliPayExecutor : PaymentExecutor {
    override val type: PaymentMethod.Type
        get() = PaymentMethod.Type.ALIPAY

    override fun apply(payParam: PayParam): PaymentOrderUnverifiedVO {
        TODO("Not yet implemented")
    }

    override fun confirm(paymentOrderNo: String, phone: String, smsCode: String): PaymentOrder.Status {
        TODO("Not yet implemented")
    }

    override fun queryStatus(paymentOrderNo: String): PaymentOrder.Status {
        TODO("Not yet implemented")
    }
}