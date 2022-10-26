package com.rainbow.pangu.api.controller

import com.rainbow.pangu.annotation.LoginCheck
import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.api.service.OrderService
import com.rainbow.pangu.base.ResultBody
import com.rainbow.pangu.threadholder.ClientInfoHolder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

@RestController
@Tag(name = "订单")
class OrderController {
    @Resource
    lateinit var orderService: OrderService

    @PostMapping("/order/goodsItem/{goodsItemId}")
    @Operation(summary = "创建普通订单")
    @LoginCheck(lock = true, checkSign = true)
    fun create(@PathVariable goodsItemId: Int, @RequestBody payParam: PayParam): ResultBody<PaymentOrderUnverifiedVO> {
        payParam.ip = ClientInfoHolder.ip
        payParam.userId = ClientInfoHolder.userId
        return ResultBody.ok(orderService.create(listOf(goodsItemId), payParam))
    }
}