package com.rainbow.pangu.api.controller

import com.rainbow.pangu.annotation.LoginCheck
import com.rainbow.pangu.api.model.vo.GoodsItemVO
import com.rainbow.pangu.api.model.vo.GoodsOwnVO
import com.rainbow.pangu.api.model.vo.GoodsVO
import com.rainbow.pangu.api.service.GoodsService
import com.rainbow.pangu.base.ResultBody
import com.rainbow.pangu.threadholder.ClientInfoHolder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

@RestController
@Tag(name = "商品")
class GoodsController {
    @Resource
    lateinit var goodsService: GoodsService

    @GetMapping("/goods")
    @Operation(summary = "商品列表")
    fun goodsList(@RequestParam(defaultValue = "1") page: Int): ResultBody<List<GoodsVO>> {
        return ResultBody.ok(goodsService.goodsList(page))
    }

    @GetMapping("/goods/item/onSale")
    @Operation(summary = "寄售资产列表")
    fun onSaleItemList(
        @RequestParam goodsId: Int,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResultBody<List<GoodsItemVO>> {
        return ResultBody.ok(goodsService.onSaleItemList(goodsId, page))
    }

    @GetMapping("/goods/own")
    @Operation(summary = "持有商品列表")
    @LoginCheck
    fun goodsOwnList(@RequestParam(defaultValue = "1") page: Int): ResultBody<List<GoodsOwnVO>> {
        var vos: List<GoodsOwnVO> = listOf()
        if (page == 1) {
            val userId = ClientInfoHolder.userId
            vos = goodsService.goodsOwnList(userId)
        }
        return ResultBody.ok(vos)
    }

    @GetMapping("/goods/item/own")
    @Operation(summary = "持有资产列表")
    @LoginCheck
    fun goodsItemOwnList(
        @RequestParam goodsId: Int,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResultBody<List<GoodsItemVO>> {
        var vos: List<GoodsItemVO> = listOf()
        if (page == 1) {
            val userId = ClientInfoHolder.userId
            vos = goodsService.goodsItemOwnList(userId, goodsId)
        }
        return ResultBody.ok(vos)
    }

}