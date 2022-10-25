package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `order_info` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "order_info", comment = "订单")
@javax.persistence.Table(
    name = "order_info",
    indexes = [Index(name = "idx_user_id", columnList = "userId"), Index(name = "idx_status", columnList = "status")]
)
class OrderInfo : BaseEntity() {
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '订单号'")
    var orderNo = ""

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '用户ID'")
    var userId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '商品ID'")
    var goodsId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '商品数量'")
    var goodsCount = 0

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0' COMMENT '金额'")
    var amount = 0L

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0' COMMENT '卖方手续费'")
    var sellerFee = 0L

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0' COMMENT '买方手续费'")
    var buyerFee = 0L

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '状态(INIT/SUCCESS/FAIL)'")
    var status = Status.INIT

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0' COMMENT '是否支付成功'")
    var paid = false

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0' COMMENT '是否退款'")
    var refunded = false

    override fun toString(): String {
        return "OrderInfo(id=$id, orderNo='$orderNo', userId=$userId, goodsId=$goodsId, goodsCount=$goodsCount, amount=$amount, sellerFee=$sellerFee, buyerFee=$buyerFee, status=$status, paid=$paid, refunded=$refunded)"
    }

    enum class Status {
        INIT, SUCCESS, FAIL
    }
}