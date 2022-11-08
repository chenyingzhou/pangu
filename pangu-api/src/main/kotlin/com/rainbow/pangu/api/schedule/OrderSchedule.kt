package com.rainbow.pangu.api.schedule

import com.rainbow.pangu.annotation.ScheduleLocker
import com.rainbow.pangu.api.service.OrderService
import com.rainbow.pangu.entity.OrderInfo
import com.rainbow.pangu.repository.OrderInfoRepo
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component
class OrderSchedule {
    @Resource
    lateinit var orderInfoRepo: OrderInfoRepo

    @Resource
    lateinit var orderService: OrderService

    @Scheduled(cron = "10,40 * * * * ?")
    @ScheduleLocker
    fun check() {
        val orderInfos = orderInfoRepo.findByStatusIn(setOf(OrderInfo.Status.INIT))

    }
}