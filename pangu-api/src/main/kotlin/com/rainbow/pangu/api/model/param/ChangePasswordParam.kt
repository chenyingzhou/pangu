package com.rainbow.pangu.api.model.param

import com.rainbow.pangu.entity.UserPassword
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "修改密码参数")
class ChangePasswordParam {
    @Schema(description = "用户ID", hidden = true)
    var userId: Int = 0

    @Schema(description = "密码类型", title = "密码类型")
    var type: UserPassword.Type = UserPassword.Type.LOGIN

    @Schema(description = "验证码")
    var code: String = ""

    @Schema(description = "原密码")
    var oldPassword: String = ""

    @Schema(description = "新密码")
    var newPassword: String = ""
}