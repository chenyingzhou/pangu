package com.rainbow.pangu.threadholder

import com.rainbow.pangu.base.BaseEntity
import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.util.RedisUtil
import kotlin.reflect.KClass

/**
 * 实体内存缓存，同时管理redis缓存
 */
object EntityHolder {
    /**
     * 是否启用内存缓存，开启则内存缓存在当前线程生效，关闭则仅作为redis缓存的代理
     */
    private val enable = ThreadLocal.withInitial { false }

    /**
     * 存储的Entity对象 class -> id -> entity
     */
    private val holder = ThreadLocal.withInitial<MutableMap<KClass<*>, MutableMap<Int, BaseEntity>>> { HashMap() }

    /**
     * enable为true时，即将删除的缓存保存在这里，线程结束时统一清理
     */
    private val deleteCacheHolder =
        ThreadLocal.withInitial<MutableMap<KClass<out BaseEntity>, MutableSet<Int>>> { HashMap() }

    /**
     * 开启内存缓存，一般在处理请求和定时任务之前执行
     */
    fun enable() {
        enable.set(true)
    }

    /**
     * 关闭并清理内存缓存，同时删除对应的redis缓存，一般在处理或定时任务结束时执行
     */
    fun disable() {
        enable.set(false)
        holder.get().clear()
        try {
            deleteCacheHolder.get().forEach { EntityCacheManager.delete(it.key, it.value) }
        } finally {
            deleteCacheHolder.get().clear()
        }
    }

    /**
     * 获取entity
     */
    fun <T : BaseEntity> find(entityClass: KClass<T>, ids: Iterable<Int>): MutableList<T> {
        val entities: MutableList<T> = ArrayList()
        val missedIds: MutableSet<Int> = HashSet()
        // 从内存获取
        val entityMap = holder.get()
        for (id in ids) {
            if (entityMap.containsKey(entityClass) && entityMap[entityClass]!!.containsKey(id)) {
                entities.add(entityMap[entityClass]!![id] as T)
            } else {
                missedIds.add(id)
            }
        }
        // 内存中未保存的，从缓存获取
        val missedEntities = EntityCacheManager.find(entityClass, missedIds)
        entities.addAll(missedEntities)
        store(entityClass, missedEntities)
        // 标记为需要删除缓存的数据，过滤之，以防当前删除/修改的数据从缓存拿到过期的值(缓存是延迟删除的)
        if (deleteCacheHolder.get().containsKey(entityClass)) {
            val deleteCacheIds: Set<Int> = deleteCacheHolder.get()[entityClass] ?: HashSet()
            entities.removeIf { deleteCacheIds.contains(it.id) }
        }
        return entities
    }

    /**
     * 暂存entity
     */
    private fun <T : BaseEntity> store(entityClass: KClass<T>, entities: Iterable<T>) {
        if (!enable.get()) {
            return
        }
        val entityMap = holder.get()
        for (entity in entities) {
            if (entity.id == 0) {
                continue
            }
            if (!entityMap.containsKey(entityClass)) {
                entityMap[entityClass] = HashMap()
            }
            entityMap[entityClass]!![entity.id!!] = entity
        }
    }

    /**
     * 保存缓存并暂存entity
     */
    fun <T : BaseEntity> saveCache(entityClass: KClass<T>, entities: Iterable<T>) {
        store(entityClass, entities)
        EntityCacheManager.store(entityClass, entities)
    }

    /**
     * 删除缓存(如果开启了thread holder，则会延迟删除)
     */
    fun <T : BaseEntity> deleteCache(entityClass: KClass<T>, ids: Iterable<Int>) {
        if (!enable.get()) {
            EntityCacheManager.delete(entityClass, ids)
            return
        }
        val deleteCacheMap = deleteCacheHolder.get()
        val exIds = deleteCacheMap.getOrDefault(entityClass, HashSet())
        ids.forEach { exIds.add(it) }
        deleteCacheMap[entityClass] = exIds
    }

    private object EntityCacheManager {

        private const val expireTime: Long = 1800

        /**
         * 从缓存获取entity
         */
        fun <T : BaseEntity> find(entityClass: KClass<T>, ids: Iterable<Int>): List<T> {
            val keys: MutableList<String> = ArrayList()
            ids.forEach { keys.add(KeyTemplate.ENTITY.fill(entityClass.simpleName, it.toString())) }
            return RedisUtil.getMulti(keys, entityClass)
        }

        /**
         * 缓存entity
         */
        fun <T : BaseEntity> store(entityClass: KClass<T>, entities: Iterable<T>) {
            val map: MutableMap<String, T> = HashMap()
            entities.forEach {
                val cacheKey = KeyTemplate.ENTITY.fill(entityClass.simpleName, it.id.toString())
                map[cacheKey] = it
            }
            RedisUtil.store(map, expireTime, 300L)
        }

        /**
         * 删除entity缓存
         */
        fun <T : BaseEntity> delete(entityClass: KClass<T>, ids: Iterable<Int>) {
            val keys: MutableList<String> = ArrayList()
            ids.forEach { keys.add(KeyTemplate.ENTITY.fill(entityClass.simpleName, it.toString())) }
            RedisUtil.del(keys)
        }
    }
}