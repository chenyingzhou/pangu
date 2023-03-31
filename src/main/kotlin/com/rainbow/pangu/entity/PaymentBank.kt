package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `payment_bank` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "payment_bank", comment = "银行")
@jakarta.persistence.Table(name = "payment_bank")
class PaymentBank : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<PaymentBank>

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("支付方式编码")
    var methodType = PaymentMethod.Type.KFT

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("银行编码")
    var bankCode = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("银行名称")
    var bankName = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("银行图标")
    var bankIcon = ""

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("单笔限额")
    var singleLimit = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("单日限额")
    var dayLimit = 0

    override fun toString(): String {
        return "PaymentBank(id=$id, methodType=$methodType, bankCode='$bankCode', bankName='$bankName', bankIcon='$bankIcon', singleLimit=$singleLimit, dayLimit=$dayLimit)"
    }
}