package com.rainbow.pangu.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "用户地址")
class UserAddressVO {
    @Schema(description = "姓名")
    var name = ""

    @Schema(description = "手机号")
    var phoneNo = ""

    @Schema(description = "地址")
    var address = ""
}