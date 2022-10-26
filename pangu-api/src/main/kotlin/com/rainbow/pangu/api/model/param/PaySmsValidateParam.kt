package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "支付短信验证参数")
class PaySmsValidateParam {
    @Schema(description = "支付单号")
    val paymentOrderNo = ""

    @Schema(description = "短信验证码")
    val smsCode = ""
}