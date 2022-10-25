package com.rainbow.pangu.api.service

import com.rainbow.pangu.api.model.vo.PaymentAccountVO
import com.rainbow.pangu.api.model.vo.PaymentMethodVO
import com.rainbow.pangu.api.model.vo.converter.PaymentAccountVOConv
import com.rainbow.pangu.api.model.vo.converter.PaymentMethodVOConv
import com.rainbow.pangu.constant.Platform
import com.rainbow.pangu.repository.PaymentAccountRepo
import com.rainbow.pangu.repository.PaymentMethodRepo
import org.springframework.stereotype.Service
import javax.annotation.Resource

@Service
class PayService {
    @Resource
    lateinit var paymentMethodRepo: PaymentMethodRepo

    @Resource
    lateinit var paymentAccountRepo: PaymentAccountRepo

    fun methodList(platform: Platform, version: Int): List<PaymentMethodVO> {
        val paymentMethods = paymentMethodRepo.findAll()
            .filter { it.platform == platform && version >= it.versionMin && version <= it.versionMax }
        return PaymentMethodVOConv.fromEntity(paymentMethods)
    }

    fun accountList(userId: Int): List<PaymentAccountVO> {
        val accountList = paymentAccountRepo.findAllByUserId(userId)
        return PaymentAccountVOConv.fromEntity(accountList)
    }
}