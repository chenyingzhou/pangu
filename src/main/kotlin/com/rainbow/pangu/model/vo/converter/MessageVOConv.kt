package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.Message
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.model.vo.MessageVO
import com.rainbow.pangu.repository.BaseRepo
import com.rainbow.pangu.repository.GoodsRepo
import com.rainbow.pangu.repository.UserRepo

object MessageVOConv : Converter<Message, MessageVO> {
    private val userRepo: UserRepo
        get() = BaseRepo.instance(UserRepo::class)
    private val goodsRepo: GoodsRepo
        get() = BaseRepo.instance(GoodsRepo::class)

    override fun fromEntity(s: Message): MessageVO {
        val vo = MessageVO()
        vo.type = s.type
        vo.content = s.content
        vo.watched = s.watched
        if (s.senderId > 0) vo.sender = UserShortVOConv.fromEntity(userRepo.findById(s.senderId).orElseGet { User() })
        if (s.goodsId > 0) vo.goods = GoodsVOConv.fromEntity(goodsRepo.findById(s.goodsId).orElseGet { Goods() })
        return vo
    }

    override fun prepare(ss: Iterable<Message>) {
        val goodsIds = ss.map { it.goodsId }.filter { it > 0 }
        var userIds = ss.map { it.senderId }.filter { it > 0 }
        if (goodsIds.isNotEmpty()) {
            val goodsList = goodsRepo.findAllById(goodsIds)
            userIds = userIds + goodsList.map { it.creatorId }
        }
        if (userIds.isNotEmpty()) userRepo.findAllById(userIds)
    }
}