package com.rainbow.pangu.api.model.param

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DEMO参数")
data class DemoParam(
    @Schema(description = "ID")
    public var id: Int? = null,
    @Schema(description = "名称")
    public var name: String = "",
)