package com.rainbow.pangu.api.model.vo

import com.rainbow.pangu.constant.Platform
import com.rainbow.pangu.entity.PaymentMethod
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "支付方式")
class PaymentMethodVO {
    @Schema(description = "ID")
    var id = 0

    @Schema(description = "编码")
    var type = PaymentMethod.Type.BALANCE

    @Schema(description = "最低版本")
    var versionMin = 0

    @Schema(description = "最高版本")
    var versionMax = 0

    @Schema(description = "平台")
    var platform: Platform = Platform.H5
}