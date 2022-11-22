package com.rainbow.pangu.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户简要信息")
class UserShortVO {
    @Schema(description = "昵称")
    var nickName = ""

    @Schema(description = "头像")
    var avatar = ""

    @Schema(description = "个性签名")
    var signature = ""
}