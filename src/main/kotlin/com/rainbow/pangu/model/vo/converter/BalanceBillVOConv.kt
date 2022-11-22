package com.rainbow.pangu.model.vo.converter

import com.rainbow.pangu.entity.BalanceBill
import com.rainbow.pangu.model.vo.BalanceBillVO
import com.rainbow.pangu.util.DateTimeUtil

object BalanceBillVOConv : Converter<BalanceBill, BalanceBillVO> {
    override fun fromEntity(s: BalanceBill): BalanceBillVO {
        val vo = BalanceBillVO()
        vo.type = s.type
        vo.status = s.status
        vo.amount = s.amount
        vo.before = s.before
        vo.after = s.after
        vo.payType = s.payType
        vo.createdTime = DateTimeUtil.toTimestamp(s.createdTime)
        return vo
    }
}