package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import jakarta.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `payment_order` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "payment_order", comment = "支付订单")
@jakarta.persistence.Table(
    name = "payment_order",
    indexes = [
        Index(name = "idx_payment_order_no", columnList = "paymentOrderNo"),
        Index(name = "idx_order_no", columnList = "orderNo"),
    ]
)
class PaymentOrder : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<PaymentOrder>

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("支付单号")
    var paymentOrderNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("订单号或余额明细编号")
    var orderNo = ""

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("金额(提现时为负数)")
    var amount = 0L

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("支付订单状态")
    var status = Status.INIT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("支付方式")
    var type = PaymentMethod.Type.BALANCE

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("支付账号ID")
    var accountId = 0

    override fun toString(): String {
        return "PaymentOrder(id=$id, paymentOrderNo='$paymentOrderNo', orderNo='$orderNo', amount=$amount, status=$status, type=$type, accountId=$accountId)"
    }

    enum class Status {
        // 创建
        INIT,

        // 处理中
        PENDING,

        // 成功
        SUCCESS,

        // 失败
        FAIL
    }
}