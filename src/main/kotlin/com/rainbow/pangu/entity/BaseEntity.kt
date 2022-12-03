package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.threadholder.EntityHolder
import com.rainbow.pangu.util.AppCtxtUtil
import org.hibernate.Hibernate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.time.LocalDateTime
import javax.persistence.*
import kotlin.reflect.KClass

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
        nullable = false,
        columnDefinition = "int COMMENT 'ID'"
    )
    open var id: Int = 0

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

    override fun toString(): String {
        return "BaseEntity(id=$id, deleted=$deleted, version=$version, createdTime=$createdTime, updatedTime=$updatedTime)"
    }

    fun save() {
        val repository = repo(this::class) as JpaRepository<BaseEntity, Int>
        repository.save(this)
        EntityHolder.deleteCache(this::class, listOf(this.id))
    }

    fun delete() {
        val repository = repo(this::class) as JpaRepository<BaseEntity, Int>
        repository.delete(this)
        EntityHolder.deleteCache(this::class, listOf(this.id))
    }

    companion object {
        private val repos = HashMap<KClass<*>, JpaRepository<*, Int>>()
        fun <Entity : Any> repo(entityClass: KClass<Entity>): JpaRepository<Entity, Int> {
            if (!repos.containsKey(entityClass)) {
                synchronized(repos) {
                    if (!repos.containsKey(entityClass)) {
                        val em = AppCtxtUtil.getBean(EntityManager::class)
                        repos[entityClass] = SimpleJpaRepository(entityClass.java, em)
                    }
                }
            }
            return repos[entityClass] as JpaRepository<Entity, Int>
        }
    }
}