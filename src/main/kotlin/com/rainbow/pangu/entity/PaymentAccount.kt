package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import jakarta.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `payment_account` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "payment_account", comment = "支付账号")
@jakarta.persistence.Table(name = "payment_account", indexes = [Index(name = "idx_user_id", columnList = "userId")])
class PaymentAccount : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<PaymentAccount>

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("用户ID")
    var userId = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("支付方式编码")
    var methodType = PaymentMethod.Type.BALANCE

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("用户名")
    var accountName = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("手机号")
    var phoneNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("身份证号")
    var idCardNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("银行卡号")
    var accountNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("银行编码")
    var bankCode = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("银行名称")
    var bankName = ""

    @Column(nullable = false, columnDefinition = "tinyint DEFAULT '0'")
    @Comment("是否支付成功")
    var paid = false

    override fun toString(): String {
        return "PaymentAccount(id=$id, userId=$userId, methodType=$methodType, accountName='$accountName', phoneNo='$phoneNo', idCardNo='$idCardNo', accountNo='$accountNo', bankCode='$bankCode', bankName='$bankName', paid=$paid)"
    }
}