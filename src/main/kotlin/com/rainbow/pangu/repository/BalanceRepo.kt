package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.Balance
import java.util.*

interface BalanceRepo : BaseRepo<Balance> {
    fun findByUserId(userId: Int): Optional<Balance>
}