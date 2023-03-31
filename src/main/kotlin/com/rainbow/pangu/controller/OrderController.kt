package com.rainbow.pangu.controller

import com.rainbow.pangu.enhance.annotation.LoginCheck
import com.rainbow.pangu.enhance.threadholder.ClientInfoHolder
import com.rainbow.pangu.model.ResultBody
import com.rainbow.pangu.model.param.PayParam
import com.rainbow.pangu.model.vo.OrderItemForMeVO
import com.rainbow.pangu.model.vo.OrderItemVO
import com.rainbow.pangu.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import jakarta.annotation.Resource

@RestController
@Tag(name = "订单")
class OrderController {
    @Resource
    lateinit var orderService: OrderService

    @PostMapping("/order/goods/{goodsId}/count/{count}")
    @Operation(summary = "创建一级订单")
    @LoginCheck(lock = true, checkSign = true)
    fun createByGoodsId(
        @PathVariable goodsId: Int,
        @PathVariable count: Int,
        @RequestBody payParam: PayParam,
    ): ResultBody<PaymentOrderUnverifiedVO> {
        payParam.ip = ClientInfoHolder.ip
        payParam.userId = ClientInfoHolder.userId
        return ResultBody.ok(orderService.createByGoodsId(goodsId, count, payParam))
    }

    @PostMapping("/order/goodsItem/{goodsItemId}")
    @Operation(summary = "创建二级订单")
    @LoginCheck(lock = true, checkSign = true)
    fun create(@PathVariable goodsItemId: Int, @RequestBody payParam: PayParam): ResultBody<PaymentOrderUnverifiedVO> {
        payParam.ip = ClientInfoHolder.ip
        payParam.userId = ClientInfoHolder.userId
        return ResultBody.ok(orderService.createByGoodsItemId(goodsItemId, payParam))
    }

    @GetMapping("/order/item/goods/{goodsId}")
    @Operation(summary = "交易(买入)记录")
    fun itemList(
        @PathVariable goodsId: Int,
        @RequestParam(defaultValue = "1") page: Int
    ): ResultBody<List<OrderItemVO>> {
        return ResultBody.ok(orderService.itemList(goodsId, page))
    }

    @GetMapping("/order/item/forMe")
    @Operation(summary = "我的交易(买入/卖出)记录")
    @LoginCheck
    fun itemListForMe(@RequestParam(defaultValue = "1") page: Int): ResultBody<List<OrderItemForMeVO>> {
        val userId = ClientInfoHolder.userId
        return ResultBody.ok(orderService.itemListForMe(userId, page))
    }
}