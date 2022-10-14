package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DEMO参数")
class DemoParam {
    @Schema(description = "ID")
    var id: Int = 0

    @Schema(description = "名称")
    var name: String = ""
}