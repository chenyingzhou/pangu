package com.rainbow.pangu.model.vo

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DemoVO")
class DemoVO {
    @Schema(description = "ID")
    var id: Int = 0

    @Schema(description = "名称")
    var name: String = ""
}