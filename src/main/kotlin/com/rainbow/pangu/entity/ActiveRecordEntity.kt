package com.rainbow.pangu.entity

import org.hibernate.Hibernate
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import jakarta.persistence.*

@MappedSuperclass
abstract class ActiveRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "int")
    @Comment("ID")
    open var id: Int = 0

    @Column(nullable = false, insertable = false, updatable = false, columnDefinition = "tinyint DEFAULT '0'")
    @Comment("是否删除")
    open var deleted: Boolean = false

    @Version
    @Column(nullable = false, columnDefinition = "int DEFAULT '0'")
    @Comment("版本")
    open var version: Int = 0

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP")
    @Comment("创建时间")
    open lateinit var createdTime: LocalDateTime

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Comment("更新时间")
    open lateinit var updatedTime: LocalDateTime

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        val that = other as ActiveRecordEntity
        return id == that.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "BaseEntity(id=$id, deleted=$deleted, version=$version, createdTime=$createdTime, updatedTime=$updatedTime)"
    }

    fun save() = ActiveRecordCompanion.save(this)

    fun delete() = ActiveRecordCompanion.delete(this)
}