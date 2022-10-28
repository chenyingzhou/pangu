package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.OrderItem

interface OrderItemRepo : BaseRepo<OrderItem> {
    fun findAllByOrderId(orderId: Int): List<OrderItem>
}