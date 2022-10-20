package com.rainbow.pangu.api.controller

import com.rainbow.pangu.api.model.vo.GoodsItemVO
import com.rainbow.pangu.api.model.vo.GoodsVO
import com.rainbow.pangu.api.service.GoodsService
import com.rainbow.pangu.base.ResultBody
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

@RestController
@Tag(name = "商品")
class GoodsController {
    @Resource
    lateinit var goodsService: GoodsService

    @PostMapping("/goods")
    @Operation(summary = "商品列表")
    fun goodsList(@RequestParam(defaultValue = "1") page: Int): ResultBody<List<GoodsVO>> {
        return ResultBody.ok(goodsService.goodsList(page))
    }

    @PostMapping("/goods/item/onSale")
    @Operation(summary = "寄售资产列表")
    fun onSaleItemList(
        @RequestParam goodsId: Int,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResultBody<List<GoodsItemVO>> {
        return ResultBody.ok(goodsService.onSaleItemList(goodsId, page))
    }
}