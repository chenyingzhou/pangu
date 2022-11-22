package com.rainbow.pangu.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "修改个人信息参数")
class EditUserParam {
    @Schema(description = "ID", hidden = true)
    var userId = 0

    @Schema(description = "昵称")
    var nickName = ""

    @Schema(description = "头像")
    var avatar = ""

    @Schema(description = "个性签名")
    var signature = ""

    @Schema(description = "个人描述")
    var description = ""

}