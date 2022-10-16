package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.vo.BalanceBillVO
import com.rainbow.pangu.api.model.vo.converter.BalanceBillVOConv
import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.entity.Balance
import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.repository.BalanceBillRepo
import com.rainbow.pangu.repository.BalanceRepo
import com.rainbow.pangu.util.LockUtil
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.annotation.Resource
import javax.transaction.Transactional

@Service
@Transactional(rollbackOn = [Exception::class])
class BalanceService {
    @Resource
    lateinit var balanceRepo: BalanceRepo

    @Resource
    lateinit var balanceBillRepo: BalanceBillRepo

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
}
