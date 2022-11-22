package com.rainbow.pangu.model.vo

import com.rainbow.pangu.entity.Message
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "消息")
class MessageVO {
    @Schema(description = "类型")
    var type: Message.Type = Message.Type.SYSTEM

    @Schema(description = "内容")
    var content = ""

    @Schema(description = "是否查看")
    var watched = false

    @Schema(description = "发送者(可能为null)")
    var sender: UserShortVO? = null

    @Schema(description = "商品(可能为null)")
    var goods: GoodsVO? = null
}