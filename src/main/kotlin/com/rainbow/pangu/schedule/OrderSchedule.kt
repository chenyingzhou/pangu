package com.rainbow.pangu.schedule

import com.rainbow.pangu.enhance.annotation.ScheduleLocker
import com.rainbow.pangu.service.OrderService
import com.rainbow.pangu.entity.OrderInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import jakarta.annotation.Resource

@Component
class OrderSchedule {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Resource
    lateinit var orderService: OrderService

    @Scheduled(cron = "10,40 * * * * ?")
    @ScheduleLocker
    fun check() {
        val orderInfos = OrderInfo.findAll(OrderInfo::status to OrderInfo.Status.INIT)
        for (orderInfo in orderInfos) {
            if (orderInfo.createdTime > LocalDateTime.now().minusMinutes(2)) {
                continue
            }
            try {
                orderService.check(orderInfo)
            } catch (e: Throwable) {
                log.error("checkOrder出错了,orderNo:${orderInfo.orderNo}", e)
            }
        }
    }
}