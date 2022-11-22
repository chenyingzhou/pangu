package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.model.vo.PaymentMethodVO

object PaymentMethodVOConv : Converter<PaymentMethod, PaymentMethodVO> {
    override fun fromEntity(s: PaymentMethod): PaymentMethodVO {
        val vo = PaymentMethodVO()
        vo.id = s.id
        vo.type = s.type
        vo.versionMin = s.versionMin
        vo.versionMax = s.versionMax
        vo.platform = s.platform
        return vo
    }
}