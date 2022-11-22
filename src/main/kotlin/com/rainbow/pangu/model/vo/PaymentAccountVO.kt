package com.rainbow.pangu.model.vo

import com.rainbow.pangu.entity.PaymentMethod
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "支付账号")
class PaymentAccountVO {
    @Schema(description = "ID")
    var id = 0

    @Schema(description = "支付方式编码")
    var methodType = PaymentMethod.Type.BALANCE

    @Schema(description = "用户名")
    var accountName = ""

    @Schema(description = "手机号")
    var phoneNo = ""

    @Schema(description = "身份证号")
    var idCardNo = ""

    @Schema(description = "银行卡号")
    var accountNo = ""

    @Schema(description = "银行编码")
    var bankCode = ""

    @Schema(description = "银行名称")
    var bankName = "中国银联"

    @Schema(description = "银行图标")
    var bankIcon = "/bank-icon/中国银联.png"
}