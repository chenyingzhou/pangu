package com.rainbow.pangu.entity

import com.rainbow.pangu.base.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Table
import org.hibernate.annotations.Where
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Entity
@Where(clause = "deleted = 0")
@SQLDelete(sql = "update `notice` set `deleted` = true, `version` = `version` + 1 where `id` = ? and `version` = ?")
@Table(appliesTo = "notice", comment = "公告")
@javax.persistence.Table(name = "notice")
class Notice : BaseEntity() {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '类型'")
    var type = Type.NOTICE

    @Column(nullable = false, columnDefinition = "int DEFAULT '0' COMMENT '权重(倒序)'")
    var weight = 0

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '标题'")
    var title = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '副标题'")
    var subTitle = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '预览图'")
    var previewUrl = ""

    @Column(nullable = false, columnDefinition = "varchar(255) DEFAULT '' COMMENT '正文图'")
    var contentUrl = ""

    override fun toString(): String {
        return "Notice(id=$id, type=$type, weight=$weight, title='$title', subTitle='$subTitle', previewUrl='$previewUrl', contentUrl='$contentUrl')"
    }

    enum class Type {
        BANNER, NOTICE,
    }
}