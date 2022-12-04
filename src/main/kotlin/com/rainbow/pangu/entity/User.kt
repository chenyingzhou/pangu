package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `user` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "user", comment = "用户")
@javax.persistence.Table(
    name = "user",
    indexes = [
        Index(name = "idx_phone_no", columnList = "phoneNo", unique = true),
        Index(name = "idx_creator", columnList = "creator"),
    ]
)
class User : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<User>

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("手机号码")
    var phoneNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("昵称")
    var nickName = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("头像")
    var avatar = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("个性签名")
    var signature = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("个人描述")
    var description = ""

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0'")
    @Comment("是否为创造者")
    var creator = false

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("身份证号")
    var idCardNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("真实姓名")
    var realName = ""

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0'")
    @Comment("实名认证是否验证")
    var realNameChecked = false

    override fun toString(): String {
        return "User(id=$id, phoneNo='$phoneNo', nickName='$nickName', avatar='$avatar', signature='$signature', description='$description', creator=$creator, idCardNo='$idCardNo', realName='$realName', realNameChecked=$realNameChecked)"
    }
}