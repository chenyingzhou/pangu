package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "登录参数")
class LoginParam {
    @Schema(description = "手机号")
    var phoneNo: String = ""

    @Schema(description = "密码")
    var password: String = ""

    @Schema(description = "验证码")
    var code: String = ""
}