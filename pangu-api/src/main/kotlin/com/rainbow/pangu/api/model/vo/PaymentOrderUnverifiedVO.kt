package com.rainbow.pangu.api.model.vo

import com.rainbow.pangu.entity.PaymentOrder
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "待验证支付信息")
class PaymentOrderUnverifiedVO {
    @Schema(description = "支付状态")
    var status: PaymentOrder.Status = PaymentOrder.Status.INIT

    @Schema(description = "是否需要短信验证")
    var needSmsValidate = false

    @Schema(description = "支付单号")
    var paymentOrderNo = ""

    @Schema(description = "订单号或余额明细编号")
    var orderNo = ""
}