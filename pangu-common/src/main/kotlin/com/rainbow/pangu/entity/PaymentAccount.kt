package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `payment_account` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "payment_account", comment = "支付账号")
@javax.persistence.Table(name = "payment_account", indexes = [Index(name = "idx_user_id", columnList = "userId")])
class PaymentAccount : BaseEntity() {
    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '用户ID'")
    var userId = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '支付方式编码'")
    var methodType = PaymentMethod.Type.BALANCE

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '用户名'")
    var accountName = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '手机号'")
    var phoneNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '身份证号'")
    var idCardNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '银行卡号'")
    var accountNo = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '银行编码'")
    var bankCode = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '银行名称'")
    var bankName = ""
}