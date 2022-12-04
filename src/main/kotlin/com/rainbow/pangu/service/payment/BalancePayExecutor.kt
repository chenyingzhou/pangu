package com.rainbow.pangu.service.payment

import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.model.param.PayParam
import com.rainbow.pangu.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.service.BalanceService
import com.rainbow.pangu.util.AppCtxtUtil
import com.rainbow.pangu.util.KeyUtil
import org.springframework.stereotype.Service

@Service
class BalancePayExecutor : PaymentExecutor {
    val balanceService: BalanceService by lazy { AppCtxtUtil.getBean(BalanceService::class) }

    override val type: PaymentMethod.Type
        get() = PaymentMethod.Type.BALANCE

    override fun apply(payParam: PayParam): PaymentOrderUnverifiedVO {
        // 扣减余额
        balanceService.add(BalanceBill.Type.PAY, payParam.userId, -payParam.amount)
        // 保存支付订单
        val paymentOrder = PaymentOrder().apply {
            paymentOrderNo = KeyUtil.nextKey
            orderNo = payParam.orderNo
            amount = payParam.amount
            status = PaymentOrder.Status.SUCCESS
            type = PaymentMethod.Type.BALANCE
        }
        paymentOrder.save()
        return PaymentOrderUnverifiedVO().apply {
            status = paymentOrder.status
            needSmsValidate = false
            paymentOrderNo = paymentOrder.paymentOrderNo
            orderNo = paymentOrder.orderNo
        }
    }

    override fun confirm(paymentOrderNo: String, phone: String, smsCode: String): PaymentOrder.Status {
        throw BizException("余额支付不需要短信验证")
    }

    override fun queryStatus(paymentOrderNo: String): PaymentOrder.Status {
        throw BizException("余额支付不需要查询状态")
    }
}