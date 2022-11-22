package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.User
import com.rainbow.pangu.model.vo.GoodsVO
import com.rainbow.pangu.repository.BaseRepo
import com.rainbow.pangu.repository.UserRepo
import com.rainbow.pangu.util.DateTimeUtil

object GoodsVOConv : Converter<Goods, GoodsVO> {
    private val userRepo: UserRepo
        get() = BaseRepo.instance(UserRepo::class)

    override fun fromEntity(s: Goods): GoodsVO {
        val vo = GoodsVO()
        vo.id = s.id
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
        vo.primaryTime = DateTimeUtil.toTimestamp(s.primaryTime)
        vo.secondaryTime = DateTimeUtil.toTimestamp(s.secondaryTime)
        vo.creator = UserVOConv.fromEntity(userRepo.findById(s.creatorId).orElseGet { User() })
        return vo
    }

    override fun prepare(ss: Iterable<Goods>) {
        val userIds = ss.map { it.creatorId }.distinct()
        userRepo.findAllById(userIds)
    }
}