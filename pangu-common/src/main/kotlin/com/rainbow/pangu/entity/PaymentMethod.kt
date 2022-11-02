package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import com.rainbow.pangu.constant.Platform
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Where(clause = "deleted = false")
@SQLDelete(sql = "update `payment_method` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "payment_method", comment = "支付方式")
@javax.persistence.Table(name = "payment_method")
class PaymentMethod : BaseEntity() {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '编码'")
    var type = Type.BALANCE

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '最低版本'")
    var versionMin = 0

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '最高版本'")
    var versionMax = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '平台'")
    var platform: Platform = Platform.H5

    enum class Type {
        ALIPAY, BALANCE, KFT
    }
}