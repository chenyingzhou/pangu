package com.rainbow.pangu.util

import com.rainbow.pangu.entity.PaymentBank
import com.rainbow.pangu.entity.PaymentMethod
import java.util.*

object PaymentUtil {
    val methodList: Iterable<PaymentMethod> by lazy { PaymentMethod.findAll() }
    val bankList: Iterable<PaymentBank> by lazy { PaymentBank.findAll() }

    fun getBankByTypeAndCode(type: PaymentMethod.Type, code: String): Optional<PaymentBank> {
        val paymentBank = bankList.firstOrNull { it.methodType == type && it.bankCode == code }
        return Optional.ofNullable(paymentBank)
    }
}