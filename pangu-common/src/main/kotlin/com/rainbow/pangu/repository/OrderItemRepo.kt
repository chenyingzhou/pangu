package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.OrderInfo
import com.rainbow.pangu.entity.OrderItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query

interface OrderItemRepo : BaseRepo<OrderItem> {
    fun findAllByOrderId(orderId: Int): List<OrderItem>

    fun findAllByGoodsIdAndStatusIn(goodsId: Int, status: List<OrderInfo.Status>, pageable: Pageable): Page<OrderItem>

    /**
     * 根据用户ID查询该用户的交易记录，包括买入和卖出
     */
    @Query("select o from OrderItem o where (o.userId = ?1 or o.sellerId = ?1) and o.status in ?2")
    fun findAllByUserAndStatusIn(userId: Int, status: List<OrderInfo.Status>, pageable: Pageable): Page<OrderItem>
}