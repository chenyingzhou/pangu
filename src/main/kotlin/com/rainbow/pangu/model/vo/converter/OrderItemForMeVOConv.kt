package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.enhance.threadholder.ClientInfoHolder
import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.OrderItem
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.model.vo.OrderItemForMeVO
import com.rainbow.pangu.util.DateTimeUtil

object OrderItemForMeVOConv : Converter<OrderItem, OrderItemForMeVO> {
    override fun fromEntity(s: OrderItem): OrderItemForMeVO {
        val goods = Goods.findById(s.goodsId).orElseThrow()
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
        val goodsList = Goods.findAllById(goodsIds)
        val creatorIds = goodsList.map { it.creatorId }
        User.findAllById(creatorIds)
    }
}