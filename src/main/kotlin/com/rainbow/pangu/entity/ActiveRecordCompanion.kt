package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.threadholder.EntityHolder
import com.rainbow.pangu.entity.spec.SpecBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface ActiveRecordCompanion<Entity : ActiveRecordEntity> {
    companion object {
        private val entityClassMap = ConcurrentHashMap<KClass<out ActiveRecordCompanion<*>>, KClass<*>>()
        private val repos = ConcurrentHashMap<KClass<*>, JpaRepository<*, Int>>()
        fun <Entity : ActiveRecordEntity> repo(entityClass: KClass<Entity>): JpaRepository<Entity, Int> {
            val repo = repos[entityClass]
                ?: throw IllegalStateException("Did you forget to annotate ${entityClass.simpleName}\$Companion with @ActiveRecord?")
            return repo as JpaRepository<Entity, Int>
        }

        fun <Entity : ActiveRecordEntity> save(entity: Entity) {
            val repo = repo(entity::class) as JpaRepository<Entity, Int>
            repo.save(entity)
            EntityHolder.del(entity::class, listOf(entity.id))
        }

        fun <Entity : ActiveRecordEntity> delete(entity: Entity) {
            val repo = repo(entity::class) as JpaRepository<Entity, Int>
            repo.delete(entity)
            EntityHolder.del(entity::class, listOf(entity.id))
        }
    }

    @PersistenceContext
    fun setEntityManager(entityManager: EntityManager) {
        val entityClass = entityClass()
        repos[entityClass] = SimpleJpaRepository(entityClass.java, entityManager)
    }

    private fun entityClass(): KClass<Entity> {
        val companionClass = this::class
        if (!entityClassMap.containsKey(companionClass)) {
            synchronized(entityClassMap) {
                if (!entityClassMap.containsKey(companionClass)) {
                    entityClassMap[companionClass] = Class.forName(javaClass.name.split("$")[0]).kotlin
                }
            }
        }
        return entityClassMap[companionClass] as KClass<Entity>
    }

    fun findById(id: Int): Optional<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass)
        val entities: List<Entity> = EntityHolder.get(entityClass, listOf(id))
        if (entities.isNotEmpty()) {
            return Optional.of(entities[0])
        }
        val optEntity = repo.findById(id)
        if (optEntity.isPresent) {
            EntityHolder.set(entityClass, listOf(optEntity.get()))
        }
        return optEntity
    }

    fun existsById(id: Int): Boolean {
        return findById(id).isPresent
    }

    fun findAllById(ids: Iterable<Int>): List<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass)
        // 尽量从缓存获取，并得到未命中部分的ID
        val entities = EntityHolder.get(entityClass, ids)
        val foundIds = entities.map { it.id }
        val missedIds = ids - foundIds.toSet()
        // 获取未命中部分并缓存
        val missedEntities = repo.findAllById(missedIds)
        entities.addAll(missedEntities)
        EntityHolder.set(entityClass, missedEntities)
        // 若传入ids是列表，返回结果按传入id顺序排序
        entities.sortWith(Comparator.comparingInt { ids.indexOf(it.id) })
        return entities
    }

    fun findAll(): List<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass)
        return repo.findAll()
    }

    fun saveAll(entities: Iterable<Entity>): List<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass)
        val saved = repo.saveAll(entities)
        EntityHolder.del(entityClass, entities.map { it.id })
        return saved
    }

    fun deleteById(id: Int) {
        val entityClass = entityClass()
        val repo = repo(entityClass)
        repo.deleteById(id)
        EntityHolder.del(entityClass, listOf(id))
    }

    fun deleteAllById(ids: Iterable<Int>) {
        val entityClass = entityClass()
        val repo = repo(entityClass)
        repo.deleteAllById(ids)
        EntityHolder.del(entityClass, ids)
    }

    fun deleteAll(entities: Iterable<Entity>) {
        val entityClass = entityClass()
        val repo = repo(entityClass)
        repo.deleteAll(entities)
        EntityHolder.del(entityClass, entities.map { it.id })
    }

    // 以下查询不使用缓存

    private fun paramsToSpec(map: Map<KProperty1<Entity, Any>, Any>): Specification<Entity> {
        val spec = SpecBuilder<Entity>()
        for ((property, value) in map) {
            if (value is Collection<*>) {
                spec.`in`(property, value)
            } else {
                spec.eq(property, value)
            }
        }
        return spec.build()
    }

    fun findOne(params: Map<KProperty1<Entity, Any>, Any>): Optional<Entity> {
        return findOne(paramsToSpec(params))
    }

    fun findOne(vararg params: Pair<KProperty1<Entity, Any>, Any>): Optional<Entity> {
        return findOne(paramsToSpec(params.toMap()))
    }

    fun findAll(params: Map<KProperty1<Entity, Any>, Any>): List<Entity> {
        return findAll(paramsToSpec(params))
    }

    fun findAll(vararg params: Pair<KProperty1<Entity, Any>, Any>): List<Entity> {
        return findAll(paramsToSpec(params.toMap()))
    }

    fun findAll(params: Map<KProperty1<Entity, Any>, Any>, pageable: Pageable): Page<Entity> {
        return findAll(paramsToSpec(params), pageable)
    }

    fun findAll(params: Map<KProperty1<Entity, Any>, Any>, sort: Sort): List<Entity> {
        return findAll(paramsToSpec(params), sort)
    }

    fun findOne(spec: Specification<Entity>): Optional<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass) as SimpleJpaRepository
        return repo.findOne(spec)
    }

    fun findAll(sort: Sort): List<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass) as SimpleJpaRepository
        return repo.findAll(sort)
    }

    fun findAll(pageable: Pageable): Page<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass) as SimpleJpaRepository
        return repo.findAll(pageable)
    }

    fun findAll(spec: Specification<Entity>): List<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass) as SimpleJpaRepository
        return repo.findAll(spec)
    }

    fun findAll(spec: Specification<Entity>, pageable: Pageable): Page<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass) as SimpleJpaRepository
        return repo.findAll(spec, pageable)
    }

    fun findAll(spec: Specification<Entity>, sort: Sort): List<Entity> {
        val entityClass = entityClass()
        val repo = repo(entityClass) as SimpleJpaRepository
        return repo.findAll(spec, sort)
    }
}