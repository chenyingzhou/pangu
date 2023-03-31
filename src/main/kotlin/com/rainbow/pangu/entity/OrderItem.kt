package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import jakarta.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `order_item` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "order_item", comment = "订单条目")
@jakarta.persistence.Table(
    name = "order_item",
    indexes = [
        Index(name = "idx_order_id", columnList = "orderId"),
        Index(name = "idx_user_id", columnList = "userId"),
        Index(name = "idx_seller_id", columnList = "sellerId"),
        Index(name = "idx_goods_id", columnList = "goodsId"),
    ]
)
class OrderItem : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<OrderItem>

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("订单ID")
    var orderId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("买家用户ID")
    var userId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("卖家用户ID")
    var sellerId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("商品ID")
    var goodsId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("资产ID")
    var goodsItemId = 0

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("金额")
    var amount = 0L

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("卖方手续费")
    var sellerFee = 0L

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0'")
    @Comment("买方手续费")
    var buyerFee = 0L

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("状态(INIT/SUCCESS/FAIL)")
    var status = OrderInfo.Status.INIT

    override fun toString(): String {
        return "OrderItem(id=$id, orderId=$orderId, userId=$userId, sellerId=$sellerId, goodsId=$goodsId, goodsItemId=$goodsItemId, amount=$amount, sellerFee=$sellerFee, buyerFee=$buyerFee, status=$status)"
    }
}