package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.vo.GoodsVO
import com.rainbow.pangu.api.model.vo.converter.GoodsVOConv
import com.rainbow.pangu.entity.BalanceBill
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
        val pageable = PageRequest.of(page - 1, 20, Sort.by(BalanceBill::createdTime.name).descending())
        val goodsPage = goodsRepo.findAll(pageable)
        return GoodsVOConv.fromEntity(goodsPage.toList())
    }
}
