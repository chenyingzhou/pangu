package com.rainbow.pangu.base

import org.hibernate.Hibernate
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
        nullable = false,
        columnDefinition = "int COMMENT 'ID'"
    )
    open var id: Int? = null

    @Column(
        nullable = false,
        insertable = false,
        updatable = false,
        columnDefinition = "tinyint DEFAULT '0' COMMENT '是否删除'"
    )
    open var deleted: Boolean = false

    @Version
    @Column(
        nullable = false,
        columnDefinition = "int DEFAULT '0' COMMENT '版本'"
    )
    open var version: Int = 0

    @Column(
        nullable = false,
        insertable = false,
        updatable = false,
        columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'"
    )
    open lateinit var createdTime: LocalDateTime

    @Column(
        nullable = false,
        insertable = false,
        updatable = false,
        columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'"
    )
    open lateinit var updatedTime: LocalDateTime

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        val that = other as BaseEntity
        return id == that.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}