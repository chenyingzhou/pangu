package com.rainbow.pangu.api.controller

import com.rainbow.pangu.annotation.LoginCheck
import com.rainbow.pangu.api.model.param.PaySmsValidateParam
import com.rainbow.pangu.api.model.vo.PaymentAccountVO
import com.rainbow.pangu.api.model.vo.PaymentMethodVO
import com.rainbow.pangu.api.service.PayService
import com.rainbow.pangu.base.ResultBody
import com.rainbow.pangu.threadholder.ClientInfoHolder
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

@RestController
@Tag(name = "支付")
class PayController {
    @Resource
    lateinit var payService: PayService

    @GetMapping("/pay/method")
    @Operation(summary = "支付方式列表")
    fun methodList(): ResultBody<List<PaymentMethodVO>> {
        return ResultBody.ok(payService.methodList(ClientInfoHolder.platform, ClientInfoHolder.version))
    }

    @GetMapping("/pay/account")
    @Operation(summary = "支付账号列表")
    @LoginCheck
    fun accountList(): ResultBody<List<PaymentAccountVO>> {
        return ResultBody.ok(payService.accountList(ClientInfoHolder.userId))
    }

    @PostMapping("/pay/smsValidate")
    @Operation(summary = "支付短信验证")
    @LoginCheck(lock = true, checkSign = true)
    fun smsValidate(paySmsValidateParam: PaySmsValidateParam): ResultBody<Boolean> {
        return ResultBody.ok(payService.smsValidate(paySmsValidateParam.paymentOrderNo, paySmsValidateParam.smsCode))
    }
}