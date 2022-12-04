package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.Message
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.model.vo.MessageVO

object MessageVOConv : Converter<Message, MessageVO> {
    override fun fromEntity(s: Message): MessageVO {
        val vo = MessageVO()
        vo.type = s.type
        vo.content = s.content
        vo.watched = s.watched
        if (s.senderId > 0) vo.sender = UserShortVOConv.fromEntity(User.findById(s.senderId).orElseGet { User() })
        if (s.goodsId > 0) vo.goods = GoodsVOConv.fromEntity(Goods.findById(s.goodsId).orElseGet { Goods() })
        return vo
    }

    override fun prepare(ss: Iterable<Message>) {
        val goodsIds = ss.map { it.goodsId }.filter { it > 0 }
        var userIds = ss.map { it.senderId }.filter { it > 0 }
        if (goodsIds.isNotEmpty()) {
            val goodsList = Goods.findAllById(goodsIds)
            userIds = userIds + goodsList.map { it.creatorId }
        }
        if (userIds.isNotEmpty()) User.findAllById(userIds)
    }
}