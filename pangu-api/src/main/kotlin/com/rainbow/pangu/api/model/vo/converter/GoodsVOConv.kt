package com.rainbow.pangu.api.model.vo.converter

import com.rainbow.pangu.api.model.vo.GoodsVO
import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.repository.UserRepo
import com.rainbow.pangu.util.DateTimeUtil

object GoodsVOConv : Converter<Goods, GoodsVO> {
    private val userRepo: UserRepo
        get() = BaseRepo.instance(UserRepo::class)

    override fun fromEntity(s: Goods): GoodsVO {
        val vo = GoodsVO()
        vo.name = s.name
        vo.initPrice = s.initPrice
        vo.initCount = s.initCount
        vo.nftCount = s.nftCount
        vo.sellerFeeRate = s.sellerFeeRate
        vo.buyerFeeRate = s.buyerFeeRate
        vo.imageUrl = s.imageUrl
        vo.mediaType = s.mediaType
        vo.mediaUrl = s.mediaUrl
        vo.description = s.description
        vo.secondaryTime = DateTimeUtil.toTimestamp(s.secondaryTime)
        vo.creator = UserVOConv.fromEntity(userRepo.findById(s.creatorId).orElseGet { User() })
        return vo
    }

    override fun prepare(ss: Iterable<Goods>) {
        val userIds = ss.map { it.creatorId }
        userRepo.findAllById(userIds)
    }
}