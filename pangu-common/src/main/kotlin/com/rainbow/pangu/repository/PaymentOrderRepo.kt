package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.PaymentOrder
import java.util.*

interface PaymentOrderRepo : BaseRepo<PaymentOrder> {
    fun findByPaymentOrderNo(paymentOrderNo: String): Optional<PaymentOrder>
}