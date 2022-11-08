package com.rainbow.pangu.util

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.PaymentBank
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.repository.PaymentBankRepo
import com.rainbow.pangu.repository.PaymentMethodRepo
import java.util.*

object PaymentUtil {
    val methodList: Iterable<PaymentMethod> by lazy {
        val paymentMethodRepo = BaseRepo.instance(PaymentMethodRepo::class)
        paymentMethodRepo.findAll()
    }
    val bankList: Iterable<PaymentBank> by lazy {
        val paymentBankRepo = BaseRepo.instance(PaymentBankRepo::class)
        paymentBankRepo.findAll()
    }

    fun getBankByTypeAndCode(type: PaymentMethod.Type, code: String): Optional<PaymentBank> {
        val paymentBank = bankList.firstOrNull { it.methodType == type && it.bankCode == code }
        return Optional.ofNullable(paymentBank)
    }
}