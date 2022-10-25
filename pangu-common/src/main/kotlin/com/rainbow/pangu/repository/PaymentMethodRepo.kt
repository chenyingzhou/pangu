package com.rainbow.pangu.repository

import com.rainbow.pangu.annotation.AsyncCache
import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.PaymentMethod

interface PaymentMethodRepo : BaseRepo<PaymentMethod> {
    @AsyncCache(timeout = 60 * 5)
    override fun findAll(): List<PaymentMethod>
}