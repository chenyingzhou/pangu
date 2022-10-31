package com.rainbow.pangu.api.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "商品交易记录")
class OrderItemVO {
    @Schema(description = "金额'")
    var amount = 0L

    @Schema(description = "支付时间(更新时间)'")
    var payTime = 0L

    @Schema(description = "买家")
    lateinit var buyer: UserShortVO
}