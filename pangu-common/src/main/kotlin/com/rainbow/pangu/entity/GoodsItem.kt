package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `goods_item` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "goods_item", comment = "藏品资产")
@javax.persistence.Table(
    name = "goods_item",
    indexes = [Index(name = "idx_nft_id", columnList = "nftId"), Index(name = "idx_user_id", columnList = "nftId")]
)
class GoodsItem : BaseEntity() {
    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '藏品ID'")
    var nftId = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT 'TOKEN'")
    var token = ""

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '藏家ID'")
    var userId = 0

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0' COMMENT '价格(分)'")
    var price = 0L

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0' COMMENT '是否上架'")
    var onSale = false

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0' COMMENT '是否锁定'")
    var locked = false

    override fun toString(): String {
        return "GoodsItem(id=$id, nftId=$nftId, token='$token', userId=$userId, price=$price, onSale=$onSale, locked=$locked)"
    }
}