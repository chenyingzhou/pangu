package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "发送验证码参数")
class SendSmsParam {
    @Schema(description = "手机号(登录状态下不需要)")
    var phoneNo: String = ""
}