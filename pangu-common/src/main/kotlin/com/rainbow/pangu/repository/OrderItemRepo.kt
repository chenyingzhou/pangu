package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.OrderInfo
import com.rainbow.pangu.entity.OrderItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderItemRepo : BaseRepo<OrderItem> {
    fun findAllByOrderId(orderId: Int): List<OrderItem>

    fun findAllByGoodsIdAndStatusIn(goodsId: Int, status: List<OrderInfo.Status>, pageable: Pageable): Page<OrderItem>
}