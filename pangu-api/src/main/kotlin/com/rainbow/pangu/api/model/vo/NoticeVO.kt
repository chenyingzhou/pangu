package com.rainbow.pangu.api.model.vo

import com.rainbow.pangu.entity.Notice
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "公告")
class NoticeVO {
    @Schema(description = "类型")
    var type = Notice.Type.NOTICE

    @Schema(description = "标题")
    var title = ""

    @Schema(description = "副标题")
    var subTitle = ""

    @Schema(description = "预览图")
    var previewUrl = ""

    @Schema(description = "正文图")
    var contentUrl = ""
}