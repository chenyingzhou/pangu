package com.rainbow.pangu.repository

import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.entity.GoodsItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GoodsItemRepo : BaseRepo<GoodsItem> {
    fun findAllByGoodsId(goodsId: Int, pageable: Pageable): Page<GoodsItem>
}