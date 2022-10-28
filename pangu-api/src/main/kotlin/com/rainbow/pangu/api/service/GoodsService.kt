package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.param.SaleGoodsItemParam
import com.rainbow.pangu.api.model.vo.GoodsItemVO
import com.rainbow.pangu.api.model.vo.GoodsOwnVO
import com.rainbow.pangu.api.model.vo.GoodsVO
import com.rainbow.pangu.api.model.vo.converter.GoodsItemVOConv
import com.rainbow.pangu.api.model.vo.converter.GoodsVOConv
import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.GoodsItem
import com.rainbow.pangu.exception.BizException
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

    fun goodsDetail(goodsId: Int): GoodsVO {
        val goods = goodsRepo.findById(goodsId).orElseThrow()
        return GoodsVOConv.fromEntity(goods)
    }

    fun onSaleItemList(goodsId: Int, page: Int): List<GoodsItemVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(GoodsItem::price.name).ascending())
        val goodsItemPage = goodsItemRepo.findAllByGoodsId(goodsId, pageable)
        return GoodsItemVOConv.fromEntity(goodsItemPage)
    }

    fun goodsOwnList(userId: Int): List<GoodsOwnVO> {
        var goodsItems = goodsItemRepo.findAllByUserId(userId)
        goodsItems = goodsItems.sortedByDescending { it.updatedTime }
        val goodsIds: MutableList<Int> = ArrayList()
        val goodsIdCountMap: MutableMap<Int, Int> = HashMap()
        for (goodsItem in goodsItems) {
            val goodsId = goodsItem.goodsId
            goodsIdCountMap[goodsId] = 1 + (goodsIdCountMap[goodsId] ?: 0)
            if (!goodsIds.contains(goodsId)) {
                goodsIds.add(goodsId)
            }
        }
        val goodsList = goodsRepo.findAllById(goodsIds)
        val goodsVOList = GoodsVOConv.fromEntity(goodsList)
        val goodsOwnVOList = goodsVOList.map {
            GoodsOwnVO().apply {
                this.count = goodsIdCountMap[it.id]!!
                this.goods = it
            }
        }
        return goodsOwnVOList
    }

    fun goodsItemOwnList(userId: Int, goodsId: Int): List<GoodsItemVO> {
        val goodsItems = goodsItemRepo.findAllByUserId(userId).filter { it.goodsId == goodsId }
        return GoodsItemVOConv.fromEntity(goodsItems)
    }

    fun saleGoodsItem(saleGoodsItemParam: SaleGoodsItemParam): Boolean {
        val goodsItem = goodsItemRepo.findById(saleGoodsItemParam.goodsItemId).orElseThrow()
        if (goodsItem.userId != saleGoodsItemParam.userId) {
            throw BizException("不能操作他人资产")
        }
        if (goodsItem.locked) {
            throw BizException("该资产正在支付中")
        }
        goodsItem.onSale = saleGoodsItemParam.sale
        if (goodsItem.onSale) goodsItem.price = saleGoodsItemParam.price
        goodsItemRepo.save(goodsItem)
        return true
    }
}
