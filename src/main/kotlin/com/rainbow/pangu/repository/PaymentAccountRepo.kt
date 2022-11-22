package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.PaymentAccount

interface PaymentAccountRepo : BaseRepo<PaymentAccount> {
    fun findAllByUserId(userId: Int): List<PaymentAccount>
}