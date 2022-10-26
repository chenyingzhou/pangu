package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `payment_order` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "payment_order", comment = "支付订单")
@javax.persistence.Table(
    name = "payment_order",
    indexes = [
        Index(name = "idx_payment_order_no", columnList = "paymentOrderNo"),
        Index(name = "idx_order_no", columnList = "orderNo"),
    ]
)
class PaymentOrder : BaseEntity() {
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '支付单号'")
    var paymentOrderNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '订单号或余额明细编号'")
    var orderNo = ""

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0' COMMENT '金额(提现时为负数)'")
    var amount = 0L

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '支付订单状态'")
    var status = Status.INIT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '支付方式'")
    var type = PaymentMethod.Type.BALANCE

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '支付账号ID'")
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