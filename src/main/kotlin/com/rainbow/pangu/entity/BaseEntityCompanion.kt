package com.rainbow.pangu.entity

import com.rainbow.pangu.enhance.threadholder.EntityHolder
import java.util.*
import kotlin.reflect.KClass

interface BaseEntityCompanion<Entity : BaseEntity> {
    private fun entityClass(): KClass<Entity> {
        return Class.forName(javaClass.name.split("$")[0]).kotlin as KClass<Entity>
    }

    fun findById(id: Int): Optional<Entity> {
        val entityClass = entityClass()
        val entities: List<Entity> = EntityHolder.find(entityClass, listOf(id))
        if (entities.isNotEmpty()) {
            return Optional.of(entities[0])
        }
        val repo = BaseEntity.repo(entityClass)
        val optEntity = repo.findById(id)
        if (optEntity.isPresent) {
            EntityHolder.saveCache(entityClass, listOf(optEntity.get()))
        }
        return optEntity
    }

    fun existsById(id: Int): Boolean {
        return findById(id).isPresent
    }

    fun findAllById(ids: Iterable<Int>): List<Entity> {
        val entityClass = entityClass()
        // 尽量从缓存获取，并得到未命中部分的ID
        val entities = EntityHolder.find(entityClass, ids)
        val foundIds = entities.map { it.id }
        val missedIds = ids - foundIds.toSet()
        // 获取未命中部分并缓存
        val repo = BaseEntity.repo(entityClass)
        val missedEntities = repo.findAllById(missedIds)
        entities.addAll(missedEntities)
        EntityHolder.saveCache(entityClass, missedEntities)
        // 若传入ids是列表，返回结果按传入id顺序排序
        entities.sortWith(Comparator.comparingInt { ids.indexOf(it.id) })
        return entities
    }

    fun saveAll(entities: Iterable<Entity>): List<Entity> {
        val entityClass = entityClass()
        val repo = BaseEntity.repo(entityClass)
        val saved = repo.saveAll(entities)
        EntityHolder.deleteCache(entityClass, entities.map { it.id })
        return saved
    }

    fun deleteById(id: Int) {
        val entityClass = entityClass()
        val repo = BaseEntity.repo(entityClass)
        repo.deleteById(id)
        EntityHolder.deleteCache(entityClass, listOf(id))
    }

    fun deleteAllById(ids: Iterable<Int>) {
        val entityClass = entityClass()
        val repo = BaseEntity.repo(entityClass)
        repo.deleteAllById(ids)
        EntityHolder.deleteCache(entityClass, ids)
    }

    fun deleteAll(entities: Iterable<Entity>) {
        val entityClass = entityClass()
        val repo = BaseEntity.repo(entityClass)
        repo.deleteAll(entities)
        EntityHolder.deleteCache(entityClass, entities.map { it.id })
    }
}