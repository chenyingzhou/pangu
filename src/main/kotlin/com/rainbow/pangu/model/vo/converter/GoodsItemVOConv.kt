package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.GoodsItem
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.model.vo.GoodsItemVO
import com.rainbow.pangu.repository.BaseRepo
import com.rainbow.pangu.repository.UserRepo

object GoodsItemVOConv : Converter<GoodsItem, GoodsItemVO> {
    private val userRepo: UserRepo
        get() = BaseRepo.instance(UserRepo::class)

    override fun fromEntity(s: GoodsItem): GoodsItemVO {
        val user = if (s.userId == 0) User() else userRepo.findById(s.userId).orElseThrow()
        val vo = GoodsItemVO()
        vo.id = s.id
        vo.token = s.token
        vo.price = s.price
        vo.onSale = s.onSale
        vo.locked = s.locked
        vo.owner = UserShortVOConv.fromEntity(user)
        return vo
    }

    override fun prepare(ss: Iterable<GoodsItem>) {
        val userIds = ss.asSequence().map { it.userId }.filter { it > 0 }.toSet()
        userRepo.findAllById(userIds)
    }
}