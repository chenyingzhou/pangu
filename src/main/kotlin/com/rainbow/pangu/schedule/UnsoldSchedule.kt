package com.rainbow.pangu.schedule

import com.rainbow.pangu.enhance.annotation.ScheduleLocker
import com.rainbow.pangu.entity.Goods
import com.rainbow.pangu.entity.GoodsItem
import com.rainbow.pangu.service.UnsoldService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.annotation.Resource

@Component
class UnsoldSchedule {
    @Resource
    lateinit var unsoldService: UnsoldService

    @Scheduled(cron = "0 */5 * * * ?")
//    @Scheduled(fixedDelay = 1L)
    @ScheduleLocker
    fun add() {
        val now = LocalDateTime.now()
        val goodsList = Goods.findAll().filter { now < it.primaryTime } //列出一级市场尚未开启的商品
        for (goods in goodsList) {
            val goodsItems = GoodsItem.findAll(mapOf(GoodsItem::goodsId to goods.id)).filter { it.onSale }
            val goodsItemIds = goodsItems.map { it.id }
            unsoldService.add(goods.id, goodsItemIds)
        }
    }
}