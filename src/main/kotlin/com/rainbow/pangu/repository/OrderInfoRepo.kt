package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.OrderInfo
import java.util.*

interface OrderInfoRepo : BaseRepo<OrderInfo> {
    fun findByOrderNo(orderNo: String): Optional<OrderInfo>

    fun findByStatusIn(statusList: Collection<OrderInfo.Status>): List<OrderInfo>
}