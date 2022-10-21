package com.rainbow.pangu.api.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "持有商品")
class GoodsOwnVO {
    @Schema(description = "ID")
    var count = 0

    @Schema(description = "商品")
    lateinit var goods: GoodsVO
}