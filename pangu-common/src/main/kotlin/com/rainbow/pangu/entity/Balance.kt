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
@SQLDelete(sql = "update `balance` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "balance", comment = "余额")
@javax.persistence.Table(name = "balance", indexes = [Index(name = "idx_user_id", columnList = "userId")])
class Balance : BaseEntity() {
    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '用户ID'")
    var userId = 0

    @Column(nullable = false, columnDefinition = "bigint DEFAULT '0' COMMENT '金额'")
    var amount = 0L

    override fun toString(): String {
        return "Balance(id=$id, userId=$userId, amount=$amount)"
    }
}