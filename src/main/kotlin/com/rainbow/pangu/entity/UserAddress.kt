package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `user_address` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "user_address", comment = "用户地址")
@jakarta.persistence.Table(
    name = "user_address",
    indexes = [
        Index(name = "idx_user_id", columnList = "userId", unique = true),
    ],
)
class UserAddress : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<UserAddress>

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("用户ID")
    var userId = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("姓名")
    var name = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("手机号")
    var phoneNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("地址")
    var address = ""

    override fun toString(): String {
        return "UserAddress(id=$id, userId=$userId, name='$name', phoneNo='$phoneNo', address='$address')"
    }
}