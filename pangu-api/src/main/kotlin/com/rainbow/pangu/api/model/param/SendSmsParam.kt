package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "发送验证码参数")
data class SendSmsParam(
    @Schema(description = "手机号") var phoneNo: String = ""
)