package com.rainbow.pangu.repository

import com.rainbow.pangu.entity.GoodsItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GoodsItemRepo : BaseRepo<GoodsItem> {
    fun findAllByGoodsIdAndOnSaleIn(goodsId: Int, onSale: List<Boolean>, pageable: Pageable): Page<GoodsItem>

    fun findAllByUserId(goodsId: Int): List<GoodsItem>
}