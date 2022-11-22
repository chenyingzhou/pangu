package com.rainbow.pangu.model.param

import com.rainbow.pangu.entity.PaymentMethod
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "支付参数")
class PayParam {
    @Schema(description = "支付方式编码")
    var paymentMethodType = PaymentMethod.Type.BALANCE

    @Schema(description = "支付账号ID(该参数和银行卡参数二选一)")
    var paymentAccountId = 0

    @Schema(description = "银行卡参数(该参数和支付账号ID二选一)")
    var bankParam: BankParam? = null

    @Schema(hidden = true)
    var userId = 0

    @Schema(hidden = true)
    var ip = ""

    @Schema(hidden = true)
    var amount = 0L

    @Schema(hidden = true)
    var orderNo = ""

    @Schema(description = "银行卡参数")
    class BankParam {
        @Schema(description = "银行卡号")
        var accountNo = ""

        @Schema(description = "用户名")
        var accountName = ""

        @Schema(description = "手机号")
        var phoneNo = ""

        @Schema(description = "身份证号")
        var idCardNo = ""
    }
}