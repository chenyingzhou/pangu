package com.rainbow.pangu.api.controller

import com.rainbow.pangu.api.model.vo.BalanceBillVO
import com.rainbow.pangu.api.service.BalanceService
import com.rainbow.pangu.base.ResultBody
import com.rainbow.pangu.threadholder.ClientInfoHolder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

@RestController
@Tag(name = "余额")
class BalanceController {
    @Resource
    lateinit var balanceService: BalanceService

    @GetMapping("/balance/amount")
    @Operation(summary = "查询余额")
    fun amount(): ResultBody<Long> {
        val userId: Int = ClientInfoHolder.userId
        return ResultBody.ok(balanceService.amount(userId))
    }

    @GetMapping("/balance/bill")
    @Operation(summary = "余额明细")
    fun bill(@RequestParam(defaultValue = "1") page: Int): ResultBody<List<BalanceBillVO>> {
        val userId: Int = ClientInfoHolder.userId
        return ResultBody.ok(balanceService.bill(userId, page))
    }
}