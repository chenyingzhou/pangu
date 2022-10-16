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
@SQLDelete(sql = "update `user_address` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "user_address", comment = "用户地址")
@javax.persistence.Table(
    name = "user_address",
    indexes = [
        Index(name = "idx_user_id", columnList = "userId", unique = true),
    ],
)
class UserAddress : BaseEntity() {
    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '用户ID'")
    var userId = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '姓名'")
    var name = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '手机号'")
    var phoneNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '地址'")
    var address = ""

    override fun toString(): String {
        return "UserAddress(id=$id, userId=$userId, name='$name', phoneNo='$phoneNo', address='$address')"
    }
}