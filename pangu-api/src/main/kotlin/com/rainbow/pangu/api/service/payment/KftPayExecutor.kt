package com.rainbow.pangu.api.service.payment

import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.entity.PaymentAccount
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.repository.PaymentAccountRepo
import com.rainbow.pangu.repository.PaymentOrderRepo
import com.rainbow.pangu.util.KeyUtil
import com.rainbow.pangu.util.SpringContextUtil
import org.springframework.stereotype.Service

@Service
class KftPayExecutor : PaymentExecutor {
    val paymentOrderRepo: PaymentOrderRepo by lazy { SpringContextUtil.getBean(PaymentOrderRepo::class) }
    val paymentAccountRepo: PaymentAccountRepo by lazy { SpringContextUtil.getBean(PaymentAccountRepo::class) }

    override val type: PaymentMethod.Type
        get() = PaymentMethod.Type.KFT

    override fun apply(payParam: PayParam): PaymentOrderUnverifiedVO {
        // 添加支付方式
        var paymentAccountId = payParam.paymentAccountId
        if (payParam.paymentAccountId == 0) {
            val paymentAccount = PaymentAccount().apply {
                userId = payParam.userId
                methodType = PaymentMethod.Type.KFT
                accountName = payParam.bankParam!!.accountName
                phoneNo = payParam.bankParam!!.phoneNo
                idCardNo = payParam.bankParam!!.idCardNo
                accountNo = payParam.bankParam!!.accountNo
                bankCode = "TODO"
                bankName = "TODO银行"
            }
            paymentAccountRepo.save(paymentAccount)
            paymentAccountId = paymentAccount.id
        }
        // 保存支付订单
        val paymentOrder = PaymentOrder().apply {
            paymentOrderNo = KeyUtil.nextKey
            orderNo = payParam.orderNo
            amount = payParam.amount
            status = PaymentOrder.Status.INIT
            type = PaymentMethod.Type.KFT
            accountId = paymentAccountId
        }
        paymentOrderRepo.save(paymentOrder)
        // TODO 开始申请支付
        return PaymentOrderUnverifiedVO().apply {
            status = paymentOrder.status
            needSmsValidate = false
            paymentOrderNo = paymentOrder.paymentOrderNo
            orderNo = payParam.orderNo
        }
    }
}