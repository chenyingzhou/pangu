package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.PaymentAccountVO
import com.rainbow.pangu.entity.PaymentAccount

object PaymentAccountVOConv : Converter<PaymentAccount, PaymentAccountVO> {
    override fun fromEntity(s: PaymentAccount): PaymentAccountVO {
        val vo = PaymentAccountVO()
        vo.id = s.id
        vo.methodType = s.methodType
        vo.accountName = "*".repeat(s.accountName.length - 1) + s.accountName.last()
        vo.phoneNo = "*".repeat(s.phoneNo.length - 4) + s.phoneNo.substring(s.phoneNo.length - 4)
        vo.idCardNo = "*".repeat(s.idCardNo.length - 4) + s.idCardNo.substring(s.idCardNo.length - 4)
        vo.accountNo = "*".repeat(s.accountNo.length - 4) + s.accountNo.substring(s.accountNo.length - 4)
        vo.bankCode = s.bankCode
        vo.bankName = s.bankName
        return vo
    }
}