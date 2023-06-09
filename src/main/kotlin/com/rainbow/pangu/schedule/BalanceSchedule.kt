package com.rainbow.pangu.schedule

import com.rainbow.pangu.enhance.annotation.ScheduleLocker
import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.service.BalanceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import jakarta.annotation.Resource

@Component
class BalanceSchedule {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Resource
    lateinit var balanceService: BalanceService

    @Scheduled(cron = "20,50 * * * * ?")
    @ScheduleLocker
    fun check() {
        val balanceBills = BalanceBill.findAll(BalanceBill::status to BalanceBill.Status.INIT)
        for (balanceBill in balanceBills) {
            // 充值仅检查2分钟之前的明细单
            if (balanceBill.type == BalanceBill.Type.RECHARGE) {
                if (balanceBill.createdTime > LocalDateTime.now().minusMinutes(2)) {
                    continue
                }
            }
            try {
                balanceService.check(balanceBill)
            } catch (e: Throwable) {
                log.error("checkBalanceBill出错了,billNo:${balanceBill.billNo}", e)
            }
        }
    }
}