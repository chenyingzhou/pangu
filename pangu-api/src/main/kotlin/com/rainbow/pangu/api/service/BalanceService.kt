package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.param.PayParam
import com.rainbow.pangu.api.model.vo.BalanceBillVO
import com.rainbow.pangu.api.model.vo.PaymentOrderUnverifiedVO
import com.rainbow.pangu.api.model.vo.converter.BalanceBillVOConv
import com.rainbow.pangu.api.service.payment.PaymentExecutor
import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.entity.Balance
import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.entity.PaymentMethod
import com.rainbow.pangu.entity.PaymentOrder
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.repository.BalanceBillRepo
import com.rainbow.pangu.repository.BalanceRepo
import com.rainbow.pangu.repository.PaymentOrderRepo
import com.rainbow.pangu.util.KeyUtil
import com.rainbow.pangu.util.LockUtil
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.annotation.Resource
import javax.transaction.Transactional
import kotlin.math.absoluteValue

@Service
@Transactional(rollbackOn = [Exception::class])
class BalanceService {
    @Resource
    lateinit var balanceRepo: BalanceRepo

    @Resource
    lateinit var balanceBillRepo: BalanceBillRepo

    @Resource
    lateinit var paymentOrderRepo: PaymentOrderRepo

    @Resource
    lateinit var paymentExecutors: List<PaymentExecutor>

    /**
     * 获取用户余额，若不存在则创建
     */
    private fun getOrCreateBalance(userId: Int): Balance {
        if (userId == 0) {
            return Balance()
        }
        val balance = balanceRepo.findByUserId(userId).orElseGet { Balance() }
        // 创建余额
        if (balance.id == 0) {
            val lockKey = KeyTemplate.LOCK_BALANCE.fill(userId)
            if (!LockUtil.lock(lockKey)) {
                throw BizException("创建钱包失败，请重试")
            }
            try {
                balance.userId = userId
                balanceRepo.save(balance)
            } finally {
                LockUtil.unlock(lockKey)
            }
        }
        return balance
    }

    /**
     * 获取用户余额数值
     */
    fun amount(userId: Int): Long {
        return getOrCreateBalance(userId).amount
    }

    /**
     * 余额明细列表
     */
    fun bill(userId: Int, page: Int): List<BalanceBillVO> {
        val pageable = PageRequest.of(page - 1, 20, Sort.by(BalanceBill::createdTime.name).descending())
        val balanceBillPage = balanceBillRepo.findAllByUserId(userId, pageable)
        return balanceBillPage.map { BalanceBillVOConv.fromEntity(it) }.toList()
    }

    /**
     * 增加(减少时amount为负数)用户余额
     *
     * @param type   变动类型
     * 增加(RECHARGE,SALE,ADD,WITHDRAW_REFUND)为异步处理，减少(PAY,SUBTRACT)为同步处理直接进入终态，
     * 提现(WITHDRAW)先扣减余额，异步查询状态，失败则增加余额，成功则进入终态
     * @param userId 用户ID
     * @param amount 金额(分)，可为负数
     * @return WalletBill
     */
    fun add(type: BalanceBill.Type, userId: Int, amount: Long): BalanceBill {
        val balance = getOrCreateBalance(userId)
        // 校验参数合法性
        val addTypes = setOf(
            BalanceBill.Type.RECHARGE, BalanceBill.Type.SALE, BalanceBill.Type.ADD, BalanceBill.Type.WITHDRAW_REFUND
        )
        if (addTypes.contains(type) && amount <= 0) {
            throw BizException("增加余额时，金额必须大于0")
        }
        val subTypes = setOf(
            BalanceBill.Type.WITHDRAW, BalanceBill.Type.PAY, BalanceBill.Type.SUBTRACT
        )
        if (subTypes.contains(type) && balance.amount < amount.absoluteValue) {
            throw BizException("余额不足")
        }
        // 生成钱包明细
        val balanceBill = BalanceBill()
        balanceBill.userId = userId
        balanceBill.billNo = KeyUtil.nextKey
        balanceBill.type = type
        balanceBill.amount = amount

        if (addTypes.contains(type)) {
            // 增加时，直接保存为INIT状态，异步处理
            balanceBillRepo.save(balanceBill)
        } else {
            balanceBill.before = balance.amount
            balanceBill.after = balance.amount + amount
            // 支付和虚减直接扣除，变为终态
            if (type == BalanceBill.Type.PAY || type == BalanceBill.Type.SUBTRACT) {
                balanceBill.status = BalanceBill.Status.SUCCESS
            }
            balanceBillRepo.save(balanceBill)
            balance.amount = balance.amount + amount
            balanceRepo.save(balance)
        }
        return balanceBill
    }

    fun recharge(amount: Long, payParam: PayParam): PaymentOrderUnverifiedVO {
        if (payParam.paymentMethodType == PaymentMethod.Type.BALANCE) {
            throw BizException("不支持该支付方式")
        }
        val balanceBill = add(BalanceBill.Type.RECHARGE, payParam.userId, amount)
        // 申请支付
        payParam.orderNo = balanceBill.billNo
        payParam.amount = balanceBill.amount
        val paymentExecutor = paymentExecutors.find { it.type == payParam.paymentMethodType }!!
        val paymentOrderUnverifiedVO = paymentExecutor.apply(payParam)
        // 如果支付状态为成功(若存在这种可能性)，直接确认充值状态
        if (paymentOrderUnverifiedVO.status == PaymentOrder.Status.SUCCESS) {
            balanceBill.status = BalanceBill.Status.SUCCESS
            balanceBillRepo.save(balanceBill)
        }
        return paymentOrderUnverifiedVO
    }

    fun withdraw(amount: Long, payParam: PayParam): PaymentOrderUnverifiedVO {
        if (payParam.paymentAccountId == 0) {
            throw BizException("请使用支付成功的银行卡提现")
        }
        val balanceBill = add(BalanceBill.Type.WITHDRAW, payParam.userId, -amount)
        // TODO 使用KFT
        return PaymentOrderUnverifiedVO().apply {
            orderNo = balanceBill.billNo
            paymentOrderNo = ""
            needSmsValidate = false
        }
    }

    /**
     * 检查余额明细状态，根据支付状态执行确认或取消
     */
    fun check(balanceBill: BalanceBill) {
        if (balanceBill.status != BalanceBill.Status.INIT) {
            return
        }
        val addTypes = setOf(
            BalanceBill.Type.RECHARGE, BalanceBill.Type.SALE, BalanceBill.Type.ADD, BalanceBill.Type.WITHDRAW_REFUND
        )
        // 仅处理增加的情况
        if (!addTypes.contains(balanceBill.type)) {
            return
        }
        if (balanceBill.type == BalanceBill.Type.RECHARGE) {
            // 充值需要校验状态
            val paymentOrder = paymentOrderRepo.findByOrderNo(balanceBill.billNo).orElseThrow()
            val paymentExecutor = paymentExecutors.find { it.type == paymentOrder.type }!!
            val payStatus = paymentExecutor.queryStatus(paymentOrder.paymentOrderNo)
            if (paymentOrder.status != payStatus) {
                paymentOrder.status = payStatus
                paymentOrderRepo.save(paymentOrder)
            }
            // 充值不成功的不参与后续步骤
            if (payStatus != PaymentOrder.Status.SUCCESS) {
                if (payStatus == PaymentOrder.Status.FAIL) {
                    balanceBill.status = BalanceBill.Status.FAIL
                    balanceBillRepo.save(balanceBill)
                }
                return
            }
        }
        val balance = getOrCreateBalance(balanceBill.userId)
        balanceBill.let {
            it.status = BalanceBill.Status.SUCCESS
            it.before = balance.amount
            it.after = balance.amount + it.amount
            balanceBillRepo.save(balanceBill)
        }
        balance.amount = balance.amount + balanceBill.amount
        balanceRepo.save(balance)
    }
}
