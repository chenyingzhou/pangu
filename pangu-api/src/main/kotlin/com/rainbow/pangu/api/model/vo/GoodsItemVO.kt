package com.rainbow.pangu.api.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "资产")
class GoodsItemVO {
    @Schema(description = "ID")
    var id = 0

    @Schema(description = "TOKEN")
    var token = ""

    @Schema(description = "价格(分)")
    var price = 0L

    @Schema(description = "是否上架")
    var onSale = false

    @Schema(description = "是否锁定")
    var locked = false

    @Schema(description = "藏家")
    lateinit var owner: UserShortVO
}