package com.rainbow.pangu.entity

import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
@Where(clause = "deleted = 0")
@SQLDelete(sql = "update `message` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "message", comment = "消息")
@javax.persistence.Table(name = "message")
class Message : BaseEntity() {
    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '发送者ID'")
    var senderId = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '用户ID'")
    var userId = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '0' COMMENT '类型'")
    var type: Type = Type.SYSTEM

    @Column(columnDefinition = "varchar(255) DEFAULT '0' COMMENT '内容'")
    var content = ""

    @Column(columnDefinition = "int DEFAULT '0' COMMENT '商品ID'")
    var goodsId = 0

    @Column(columnDefinition = "int DEFAULT '0' COMMENT '资产ID'")
    var goodsItemId = 0

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0' COMMENT '是否查看'")
    var watched = false

    override fun toString(): String {
        return "Message(id=$id, senderId=$senderId, userId=$userId, type=$type, content='$content', goodsId=$goodsId, goodsItemId=$goodsItemId, watched=$watched)"
    }

    enum class Type {
        SYSTEM,
        BUY,
    }
}