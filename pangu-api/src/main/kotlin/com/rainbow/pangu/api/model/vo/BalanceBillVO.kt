package com.rainbow.pangu.api.model.vo

import com.rainbow.pangu.entity.BalanceBill
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "余额明细")
class BalanceBillVO {
    @Schema(description = "类型")
    var type = BalanceBill.Type.PAY

    @Schema(description = "状态")
    var status = BalanceBill.Status.INIT

    @Schema(description = "发生金额")
    var amount = 0L

    @Schema(description = "发生前金额")
    var before = 0L

    @Schema(description = "发生后金额")
    var after = 0L

    @Schema(description = "支付方式(TODO)")
    var payType = ""

    @Schema(description = "创建时间")
    var createdTime: Long = 0
}