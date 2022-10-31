package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.OrderItemForMeVO
import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.OrderItem
import com.rainbow.pangu.repository.GoodsRepo
import com.rainbow.pangu.repository.UserRepo
import com.rainbow.pangu.threadholder.ClientInfoHolder
import com.rainbow.pangu.util.DateTimeUtil

object OrderItemForMeVOConv : Converter<OrderItem, OrderItemForMeVO> {
    private val userRepo: UserRepo
        get() = BaseRepo.instance(UserRepo::class)
    private val goodsRepo: GoodsRepo
        get() = BaseRepo.instance(GoodsRepo::class)

    override fun fromEntity(s: OrderItem): OrderItemForMeVO {
        val goods = goodsRepo.findById(s.goodsId).orElseThrow()
        val vo = OrderItemForMeVO()
        vo.payTime = DateTimeUtil.toTimestamp(s.updatedTime)
        vo.buy = s.userId == ClientInfoHolder.userId
        vo.amount = s.amount
        vo.sellerFee = s.sellerFee
        vo.buyerFee = s.buyerFee
        vo.goodsVO = GoodsVOConv.fromEntity(goods)
        return vo
    }

    override fun prepare(ss: Iterable<OrderItem>) {
        val goodsIds = ss.map { it.goodsId }
        val goodsList = goodsRepo.findAllById(goodsIds)
        val creatorIds = goodsList.map { it.creatorId }
        userRepo.findAllById(creatorIds)
    }
}