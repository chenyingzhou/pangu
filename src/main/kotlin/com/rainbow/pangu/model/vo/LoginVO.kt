package com.rainbow.pangu.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "登录信息")
class LoginVO {
    @Schema(description = "TOKEN")
    var token = ""

    @Schema(description = "是否设置了登录密码")
    var hasPassword = false

    @Schema(description = "用户信息")
    lateinit var user: UserVO
}