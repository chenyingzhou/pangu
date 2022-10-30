package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.OrderItemVO
import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.OrderItem
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.repository.UserRepo
import com.rainbow.pangu.util.DateTimeUtil

object OrderItemVOConv : Converter<OrderItem, OrderItemVO> {
    private val userRepo: UserRepo
        get() = BaseRepo.instance(UserRepo::class)

    override fun fromEntity(s: OrderItem): OrderItemVO {
        val user = userRepo.findById(s.userId).orElseGet { User() }
        val vo = OrderItemVO()
        vo.amount = s.amount
        vo.payTime = DateTimeUtil.toTimestamp(s.updatedTime)
        vo.buyer = UserShortVOConv.fromEntity(user)
        return vo
    }

    override fun prepare(ss: Iterable<OrderItem>) {
        val userIds = ss.map { it.userId }
        userRepo.findAllById(userIds)
    }
}