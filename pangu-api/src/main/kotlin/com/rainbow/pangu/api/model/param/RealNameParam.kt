package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "实名认证参数")
class RealNameParam {
    @Schema(description = "姓名")
    var realName: String = ""

    @Schema(description = "身份证号")
    var idCardNo: String = ""
}