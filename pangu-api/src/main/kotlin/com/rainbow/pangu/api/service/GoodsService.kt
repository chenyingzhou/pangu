package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.vo.GoodsItemVO
import com.rainbow.pangu.api.model.vo.GoodsVO
import com.rainbow.pangu.api.model.vo.converter.GoodsItemVOConv
import com.rainbow.pangu.api.model.vo.converter.GoodsVOConv
import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.GoodsItem
import com.rainbow.pangu.repository.GoodsItemRepo
import com.rainbow.pangu.repository.GoodsRepo
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.annotation.Resource
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class GoodsService {
    @Resource
    lateinit var goodsRepo: GoodsRepo

    @Resource
    lateinit var goodsItemRepo: GoodsItemRepo

    fun goodsList(page: Int): List<GoodsVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(Goods::createdTime.name).descending())
        val goodsPage = goodsRepo.findAll(pageable)
        return GoodsVOConv.fromEntity(goodsPage)
    }

    fun onSaleItemList(goodsId: Int, page: Int): List<GoodsItemVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(GoodsItem::price.name).ascending())
        val goodsItemPage = goodsItemRepo.findAllByGoodsId(goodsId, pageable)
        return GoodsItemVOConv.fromEntity(goodsItemPage)
    }
}
