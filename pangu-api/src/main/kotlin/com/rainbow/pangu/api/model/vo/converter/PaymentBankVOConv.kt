package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.PaymentBankVO
import com.rainbow.pangu.entity.PaymentBank

object PaymentBankVOConv : Converter<PaymentBank, PaymentBankVO> {
    override fun fromEntity(s: PaymentBank): PaymentBankVO {
        val vo = PaymentBankVO()
        vo.methodType = s.methodType
        vo.bankCode = s.bankCode
        vo.bankName = s.bankName
        vo.bankIcon = s.bankIcon
        vo.singleLimit = s.singleLimit
        vo.dayLimit = s.dayLimit
        return vo
    }
}