package com.rainbow.pangu.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "我的商品交易记录")
class OrderItemForMeVO {
    @Schema(description = "成交时间(更新时间)'")
    var payTime = 0L

    @Schema(description = "是否买入(true-买入，false-卖出)")
    var buy = false

    @Schema(description = "金额")
    var amount = 0L

    @Schema(description = "卖方手续费")
    var sellerFee = 0L

    @Schema(description = "买方手续费")
    var buyerFee = 0L

    @Schema(description = "商品")
    lateinit var goodsVO: GoodsVO
}