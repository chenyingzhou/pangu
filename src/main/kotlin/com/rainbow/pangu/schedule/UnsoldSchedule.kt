package com.rainbow.pangu.schedule

import com.rainbow.pangu.enhance.annotation.ScheduleLocker
import com.rainbow.pangu.service.UnsoldService
import com.rainbow.pangu.repository.GoodsItemRepo
import com.rainbow.pangu.repository.GoodsRepo
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.annotation.Resource

@Component
class UnsoldSchedule {
    @Resource
    lateinit var goodsRepo: GoodsRepo

    @Resource
    lateinit var goodsItemRepo: GoodsItemRepo

    @Resource
    lateinit var unsoldService: UnsoldService

    @Scheduled(cron = "0 */5 * * * ?")
//    @Scheduled(fixedDelay = 1L)
    @ScheduleLocker
    fun add() {
        val now = LocalDateTime.now()
        val goodsList = goodsRepo.findAll().filter { now < it.primaryTime } //列出一级市场尚未开启的商品
        for (goods in goodsList) {
            val goodsItems = goodsItemRepo.findAllByGoodsIdAndOnSaleIn(goods.id, listOf(true), Pageable.unpaged())
            val goodsItemIds = goodsItems.map { it.id }
            unsoldService.add(goods.id, goodsItemIds)
        }
    }
}