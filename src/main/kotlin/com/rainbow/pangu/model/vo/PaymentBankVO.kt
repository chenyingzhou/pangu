package com.rainbow.pangu.model.vo

import com.rainbow.pangu.entity.PaymentMethod
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "银行")
class PaymentBankVO {
    @Schema(description = "支付方式编码")
    var methodType = PaymentMethod.Type.KFT

    @Schema(description = "银行编码")
    var bankCode = ""

    @Schema(description = "银行名称")
    var bankName = "中国银联"

    @Schema(description = "银行图标")
    var bankIcon = "/bank-icon/中国银联.png"

    @Schema(description = "单笔限额")
    var singleLimit = 0

    @Schema(description = "单日限额")
    var dayLimit = 0
}