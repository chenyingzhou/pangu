package com.rainbow.pangu.api.model.vo

import com.rainbow.pangu.entity.Goods
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "商品")
class GoodsVO {
    @Schema(description = "ID")
    var id = 0

    @Schema(description = "名称")
    var name = ""

    @Schema(description = "发行价(分)")
    var initPrice = 0L

    @Schema(description = "发行数量")
    var initCount = 0

    @Schema(description = "流通数量")
    var nftCount = 0

    @Schema(description = "卖方手续费率(万分比)")
    var sellerFeeRate = 0

    @Schema(description = "买方手续费率(万分比)")
    var buyerFeeRate = 0

    @Schema(description = "图片地址")
    var imageUrl = ""

    @Schema(description = "媒体类型(IMAGE/MUSIC/VIDEO)")
    var mediaType = Goods.Type.IMAGE

    @Schema(description = "媒体地址")
    var mediaUrl = ""

    @Schema(description = "描述")
    var description = ""

    @Schema(description = "二级市场开放时间")
    var secondaryTime: Long = 0

    @Schema(description = "创作者")
    lateinit var creator: UserVO
}