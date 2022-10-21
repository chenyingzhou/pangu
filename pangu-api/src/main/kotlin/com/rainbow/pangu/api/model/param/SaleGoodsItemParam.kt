package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "资产上架或下架参数")
class SaleGoodsItemParam {
    @Schema(description = "用户ID", hidden = true)
    var userId = 0

    @Schema(description = "资产ID")
    var goodsItemId = 0

    @Schema(description = "上下架")
    var sale = false

    @Schema(description = "价格(分)，下架时该参数无效")
    var price = 0L
}