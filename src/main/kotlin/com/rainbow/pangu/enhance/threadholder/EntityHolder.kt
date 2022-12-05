package com.rainbow.pangu.enhance.threadholder

import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.entity.ActiveRecordEntity
import com.rainbow.pangu.util.RedisUtil
import kotlin.reflect.KClass

/**
 * 管理线程内缓存，同时管理redis缓存
 */
object EntityHolder {
    /**
     * 是否启用内存缓存，开启则内存缓存在当前线程生效，关闭则仅作为redis缓存的代理
     */
    private val enable = ThreadLocal.withInitial { false }

    /**
     * 存储的Entity对象 class -> id -> entity
     */
    private val holder = ThreadLocal.withInitial<MutableMap<KClass<*>, MutableMap<Int, Any>>> { HashMap() }

    /**
     * 即将删除的缓存保存在这里，线程结束时统一清理
     */
    private val deletedHolder = ThreadLocal.withInitial<MutableMap<KClass<*>, MutableSet<Int>>> { HashMap() }

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
            deletedHolder.get().forEach { EntityCacheManager.del(it.key, it.value) }
        } finally {
            deletedHolder.get().clear()
        }
    }

    /**
     * 暂存entity至当前线程
     */
    private fun <Entity : ActiveRecordEntity> hold(entityClass: KClass<Entity>, entities: Iterable<Entity>) {
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
            entityMap[entityClass]!![entity.id] = entity
        }
    }

    /**
     * 获取entity
     */
    fun <Entity : ActiveRecordEntity> get(entityClass: KClass<Entity>, ids: Iterable<Int>): MutableList<Entity> {
        val entities: MutableList<Entity> = ArrayList()
        val missedIds: MutableSet<Int> = HashSet()
        // 从内存获取
        val entityMap = holder.get()
        for (id in ids) {
            if (entityMap[entityClass]?.get(id) != null) {
                entities.add(entityMap[entityClass]!![id]!! as Entity)
            } else {
                missedIds.add(id)
            }
        }
        // 内存中未保存的，从缓存获取
        val missedEntities = EntityCacheManager.get(entityClass, missedIds)
        entities.addAll(missedEntities)
        hold(entityClass, missedEntities)
        // 标记为需要删除缓存的数据，过滤之，以防当前删除/修改的数据从缓存拿到过期的值(缓存是延迟删除的)
        if (deletedHolder.get().containsKey(entityClass)) {
            val deleteCacheIds: Set<Int> = deletedHolder.get()[entityClass] ?: HashSet()
            entities.removeIf { deleteCacheIds.contains(it.id) }
        }
        return entities
    }

    /**
     * 保存缓存并暂存entity
     */
    fun <Entity : ActiveRecordEntity> set(entityClass: KClass<Entity>, entities: Iterable<Entity>) {
        hold(entityClass, entities)
        EntityCacheManager.set(entityClass, entities)
    }

    /**
     * 删除缓存(如果开启了holder，则延迟删除，并在线程内增加删除标记)
     *
     * 延迟删除，即在事务完成后删除缓存，相对于立即删除，能防止其他线程在此期间读取该数据，再次写入缓存，造成数据在较长时间内不一致；
     * 同时，线程内的删除标记可以防止后续步骤读取该数据
     */
    fun <Entity : ActiveRecordEntity> del(entityClass: KClass<Entity>, ids: Iterable<Int>) {
        if (!enable.get()) {
            EntityCacheManager.del(entityClass, ids)
            return
        }
        val deletedCacheMap = deletedHolder.get()
        val exIds = deletedCacheMap.getOrDefault(entityClass, HashSet())
        ids.forEach { exIds.add(it) }
        deletedCacheMap[entityClass] = exIds
    }

    /**
     * 管理Redis缓存
     */
    private object EntityCacheManager {

        private const val expireTime: Long = 1800

        /**
         * 从缓存获取entity
         */
        fun <Entity : ActiveRecordEntity> get(entityClass: KClass<Entity>, ids: Iterable<Int>): List<Entity> {
            val keys: MutableList<String> = ArrayList()
            ids.forEach { keys.add(KeyTemplate.ENTITY.fill(entityClass.simpleName, it.toString())) }
            return RedisUtil.get(keys, entityClass)
        }

        /**
         * 缓存entity
         */
        fun <Entity : ActiveRecordEntity> set(entityClass: KClass<Entity>, entities: Iterable<Entity>) {
            val map: MutableMap<String, ActiveRecordEntity> = HashMap()
            entities.forEach {
                val cacheKey = KeyTemplate.ENTITY.fill(entityClass.simpleName, it.id.toString())
                map[cacheKey] = it
            }
            RedisUtil.set(map, expireTime, 300L)
        }

        /**
         * 删除entity缓存
         */
        fun del(entityClass: KClass<*>, ids: Iterable<Int>) {
            val keys: MutableList<String> = ArrayList()
            ids.forEach { keys.add(KeyTemplate.ENTITY.fill(entityClass.simpleName, it.toString())) }
            RedisUtil.del(keys)
        }
    }
}