package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.BalanceBill
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BalanceBillRepo : BaseRepo<BalanceBill> {
    fun findAllByUserId(userId: Int, pageable: Pageable): Page<BalanceBill>
}