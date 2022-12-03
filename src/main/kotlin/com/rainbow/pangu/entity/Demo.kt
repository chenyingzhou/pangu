package com.rainbow.pangu.entity

import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity

@Entity
@Where(clause = "deleted = 0")
@SQLDelete(sql = "update `demo` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "demo", comment = "DEMO")
@javax.persistence.Table(name = "demo")
class Demo : BaseEntity() {
    companion object : BaseEntityCompanion<Demo>

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '名称'")
    var name = ""

    override fun toString(): String {
        return "Demo(id=$id, name='$name')"
    }
}