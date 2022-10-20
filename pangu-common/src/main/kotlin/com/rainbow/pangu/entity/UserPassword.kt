package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `user_password` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "user_password", comment = "用户密码")
@javax.persistence.Table(
    name = "user_password",
    indexes = [
        Index(name = "idx_type_user_id", columnList = "type,userId", unique = true),
    ],
)
class UserPassword : BaseEntity() {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT 'LOGIN' COMMENT '密码类型'")
    var type = Type.LOGIN

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '用户ID'")
    var userId = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '密码'")
    var password = ""

    enum class Type {
        LOGIN,
    }

    override fun toString(): String {
        return "UserPassword(id=$id, type=$type, userId=$userId, password='$password')"
    }
}