package com.rainbow.pangu.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户信息")
class UserVO {
    @Schema(description = "ID")
    var id = 0

    @Schema(description = "昵称")
    var nickName = ""

    @Schema(description = "头像")
    var avatar = ""

    @Schema(description = "个性签名")
    var signature = ""

    @Schema(description = "个人描述")
    var description = ""

    @Schema(description = "是否为创造者")
    var creator = false

    @Schema(description = "是否实名认证")
    var hasRealName = false
}