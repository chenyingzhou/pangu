package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `balance_bill` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "balance_bill", comment = "余额明细")
@javax.persistence.Table(
    name = "balance_bill",
    indexes = [
        Index(name = "idx_user_id_created_time", columnList = "userId,createdTime"),
        Index(name = "idx_bill_no", columnList = "billNo"),
        Index(name = "idx_status", columnList = "status"),
    ]
)
class BalanceBill : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<BalanceBill>

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("用户ID")
    var userId = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("钱包明细编号")
    var billNo = ""

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("类型")
    var type = Type.PAY

    @Enumerated(EnumType.STRING)
    @Column(name = "`status`", nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("状态")
    var status = Status.INIT

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("发生金额")
    var amount = 0L

    @Column(name = "`before`", nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("发生前金额")
    var before = 0L

    @Column(name = "`after`", nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("发生后金额")
    var after = 0L

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("支付方式(仅用于前端展示)")
    var payType = ""

    override fun toString(): String {
        return "BalanceBill(id=$id, userId=$userId, billNo='$billNo', type=$type, status=$status, amount=$amount, before=$before, after=$after, payType='$payType')"
    }

    enum class Type {
        // 充值
        RECHARGE,

        // 提现
        WITHDRAW,

        // 购买支付
        PAY,

        // 卖出收益
        SALE,

        //提现失败退回
        WITHDRAW_REFUND,

        // 虚增
        ADD,

        // 虚减
        SUBTRACT,
    }

    enum class Status {
        INIT, SUCCESS, FAIL
    }
}