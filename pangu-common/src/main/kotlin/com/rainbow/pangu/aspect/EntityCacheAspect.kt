package com.rainbow.pangu.aspect

import com.rainbow.pangu.annotation.EntityCache
import com.rainbow.pangu.base.BaseEntity
import com.rainbow.pangu.base.BaseRepo
import com.rainbow.pangu.threadholder.EntityHolder
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.aop.framework.AopContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@Aspect
@Component
class EntityCacheAspect {
    private val repoEntityClassMap: MutableMap<BaseRepo<*>, KClass<out BaseEntity>> = ConcurrentHashMap()

    @Around("@annotation(entityCache)")
    @Throws(Throwable::class)
    fun entityCacheAround(joinPoint: ProceedingJoinPoint, entityCache: EntityCache): Any? {
        // 获取参数，且不处理无参数方法
        val arg = (if (joinPoint.args.isNotEmpty()) joinPoint.args[0] else null) ?: return joinPoint.proceed()
        // 使用缓存
        val repo = AopContext.currentProxy() as BaseRepo<*>
        val signature = joinPoint.signature as MethodSignature
        when (signature.method.name) {
            "save" -> {
                val entity = joinPoint.proceed() as BaseEntity
                val entityClass = entity::class
                putEntityClass(repo, entityClass)
                EntityHolder.deleteCache(entityClass, listOf(entity.id))
                return entity
            }

            "saveAll" -> {
                val entities = joinPoint.proceed() as List<*>
                if (entities.isNotEmpty()) {
                    val entityClass = (entities[0]!! as BaseEntity)::class
                    putEntityClass(repo, entityClass)
                    EntityHolder.deleteCache(entityClass, entities.map { (it as BaseEntity).id })
                }
                return entities
            }

            "findById" -> {
                var entityClass = repoEntityClassMap[repo]
                if (entityClass != null) {
                    val entities: List<BaseEntity> = EntityHolder.find(entityClass, listOf(arg as Int))
                    if (entities.isNotEmpty()) {
                        return Optional.of(entities[0])
                    }
                }
                val optEntity = joinPoint.proceed() as Optional<*>
                if (optEntity.isPresent) {
                    entityClass = (optEntity.get() as BaseEntity)::class
                    putEntityClass(repo, entityClass)
                    EntityHolder.saveCache(entityClass, listOf(optEntity.get() as BaseEntity))
                }
                return optEntity
            }

            "findAllById" -> {
                val ids = (arg as Iterable<*>).map { it as Int }
                var entityClass = repoEntityClassMap[repo]
                val entities = if (entityClass != null) EntityHolder.find(entityClass, ids) else ArrayList()
                val foundIds = entities.map { it.id }
                val missedIds = ids - foundIds.toSet()
                val missedEntities = (joinPoint.proceed(arrayOf<Any>(missedIds)) as List<*>).map { it as BaseEntity }
                entities.addAll(missedEntities)
                if (entityClass == null && entities.isNotEmpty()) {
                    entityClass = entities[0]::class
                    putEntityClass(repo, entityClass)
                }
                if (entityClass != null) {
                    EntityHolder.saveCache(entityClass, missedEntities)
                }
                // 若传入ids是列表，返回结果按传入id顺序排序
                entities.sortWith(Comparator.comparingInt { ids.indexOf(it.id) })
                return entities
            }

            "existsById" -> {
                val entity = repo.findById((arg as Int)).orElse(null)
                return entity != null
            }

            "deleteById" -> {
                EntityHolder.deleteCache(getEntityClassUngracefully(repo), listOf(arg as Int))
                return joinPoint.proceed()
            }

            "deleteAllById" -> {
                EntityHolder.deleteCache(getEntityClassUngracefully(repo), (arg as Iterable<*>).map { it as Int })
                return joinPoint.proceed()
            }

            "delete" -> {
                val entity = arg as BaseEntity
                putEntityClass(repo, entity::class)
                EntityHolder.deleteCache(entity::class, listOf(entity.id))
                return joinPoint.proceed()
            }

            "deleteAll" -> {
                val deleteIds: MutableList<Int> = ArrayList()
                val entities = arg as Iterable<*>
                var entityClass = repoEntityClassMap[repo]
                for (baseEntity in entities) {
                    val entity = baseEntity as BaseEntity
                    deleteIds.add(entity.id)
                    if (entityClass == null) {
                        entityClass = entity::class
                        putEntityClass(repo, entityClass)
                    }
                }
                entityClass?.let { EntityHolder.deleteCache(it, deleteIds) }
                return joinPoint.proceed()
            }
        }
        return joinPoint.proceed()
    }

    /**
     * 存储repo->entityClass
     */
    private fun putEntityClass(repo: BaseRepo<*>, entityClass: KClass<out BaseEntity>) {
        if (!repoEntityClassMap.containsKey(repo)) {
            synchronized(repo) {
                if (!repoEntityClassMap.containsKey(repo)) {
                    repoEntityClassMap[repo] = entityClass
                }
            }
        }
    }

    /**
     * 无法根据参数或返回值确定entityClass时，以该方式获取
     */
    private fun getEntityClassUngracefully(repo: BaseRepo<*>): KClass<out BaseEntity> {
        if (!repoEntityClassMap.containsKey(repo)) {
            synchronized(repo) {
                if (!repoEntityClassMap.containsKey(repo)) {
                    val entity = repo.findAll(Pageable.ofSize(1)).content[0]
                    repoEntityClassMap[repo] = entity::class as KClass<out BaseEntity>
                }
            }
        }
        return repoEntityClassMap[repo]!!
    }
}