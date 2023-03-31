package com.rainbow.pangu.service

import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.GoodsItem
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.model.param.SaleGoodsItemParam
import com.rainbow.pangu.model.vo.GoodsItemVO
import com.rainbow.pangu.model.vo.GoodsOwnVO
import com.rainbow.pangu.model.vo.GoodsVO
import com.rainbow.pangu.model.vo.converter.GoodsItemVOConv
import com.rainbow.pangu.model.vo.converter.GoodsVOConv
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class GoodsService {
    fun goodsList(page: Int): List<GoodsVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(Goods::createdTime.name).descending())
        val goodsPage = Goods.findAll(pageable)
        return GoodsVOConv.fromEntity(goodsPage)
    }

    fun goodsDetail(goodsId: Int): GoodsVO {
        val goods = Goods.findById(goodsId).orElseThrow()
        return GoodsVOConv.fromEntity(goods)
    }

    fun onSaleItemList(goodsId: Int, page: Int): List<GoodsItemVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(GoodsItem::price.name, GoodsItem::id.name).ascending())
        val goodsItemPage = GoodsItem.findAll(mapOf(GoodsItem::goodsId to goodsId, GoodsItem::onSale to true), pageable)
        return GoodsItemVOConv.fromEntity(goodsItemPage)
    }

    fun goodsOwnList(userId: Int): List<GoodsOwnVO> {
        var goodsItems = GoodsItem.findAll(GoodsItem::userId to userId)
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
        val goodsList = Goods.findAllById(goodsIds)
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
        val goodsItems = GoodsItem.findAll(GoodsItem::userId to userId).filter { it.goodsId == goodsId }
        return GoodsItemVOConv.fromEntity(goodsItems)
    }

    fun saleGoodsItem(saleGoodsItemParam: SaleGoodsItemParam): Boolean {
        val goodsItem = GoodsItem.findById(saleGoodsItemParam.goodsItemId).orElseThrow()
        if (goodsItem.userId != saleGoodsItemParam.userId) {
            throw BizException("不能操作他人资产")
        }
        if (goodsItem.locked) {
            throw BizException("该资产正在支付中")
        }
        goodsItem.onSale = saleGoodsItemParam.sale
        if (goodsItem.onSale) goodsItem.price = saleGoodsItemParam.price
        goodsItem.save()
        return true
    }
}
