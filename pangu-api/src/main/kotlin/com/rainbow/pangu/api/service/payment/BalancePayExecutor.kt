package com.rainbow.pangu.api.service.payment

import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.api.service.BalanceService
import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.repository.PaymentOrderRepo
import com.rainbow.pangu.util.KeyUtil
import com.rainbow.pangu.util.SpringContextUtil
import org.springframework.stereotype.Service

@Service
class BalancePayExecutor : PaymentExecutor {
    val balanceService: BalanceService by lazy { SpringContextUtil.getBean(BalanceService::class) }
    val paymentOrderRepo: PaymentOrderRepo by lazy { SpringContextUtil.getBean(PaymentOrderRepo::class) }

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
        paymentOrderRepo.save(paymentOrder)
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