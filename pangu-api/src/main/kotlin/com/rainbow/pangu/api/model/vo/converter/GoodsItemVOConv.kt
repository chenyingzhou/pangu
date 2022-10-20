package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.GoodsItemVO
import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.GoodsItem
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.repository.UserRepo

object GoodsItemVOConv : Converter<GoodsItem, GoodsItemVO> {
    private val userRepo: UserRepo
        get() = BaseRepo.instance(UserRepo::class)

    override fun fromEntity(s: GoodsItem): GoodsItemVO {
        val vo = GoodsItemVO()
        vo.id = s.id
        vo.token = s.token
        vo.price = s.price
        vo.onSale = s.onSale
        vo.locked = s.locked
        vo.owner = UserShortVOConv.fromEntity(userRepo.findById(s.userId).orElseGet { User() })
        return vo
    }

    override fun prepare(ss: Iterable<GoodsItem>) {
        val userIds = ss.map { it.userId }.distinct()
        userRepo.findAllById(userIds)
    }
}