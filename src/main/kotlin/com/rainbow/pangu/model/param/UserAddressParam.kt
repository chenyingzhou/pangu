package com.rainbow.pangu.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户地址参数")
class UserAddressParam {
    @Schema(description = "用户ID", hidden = true)
    var userId = 0

    @Schema(description = "姓名")
    var name = ""

    @Schema(description = "手机号")
    var phoneNo = ""

    @Schema(description = "地址")
    var address = ""
}