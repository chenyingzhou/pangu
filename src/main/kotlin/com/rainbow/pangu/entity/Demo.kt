package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.annotation.ActiveRecord
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
@Where(clause = "deleted = 0")
@SQLDelete(sql = "update `demo` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "demo", comment = "DEMO")
@jakarta.persistence.Table(name = "demo")
class Demo : ActiveRecordEntity() {
    @ActiveRecord
    companion object : ActiveRecordCompanion<Demo>

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT ''")
    @Comment("名称")
    var name = ""

    override fun toString(): String {
        return "Demo(id=$id, name='$name')"
    }
}