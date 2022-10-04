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
    private val repoEntityClassMap: MutableMap<BaseRepo<*>, KClass<BaseEntity>> = ConcurrentHashMap()

    @Around("@annotation(entityCache)")
    @Throws(Throwable::class)
    fun entityCacheAround(joinPoint: ProceedingJoinPoint, entityCache: EntityCache): Any? {
        // 获取参数，且不处理无参数方法
        val arg = (if (joinPoint.args.isNotEmpty()) joinPoint.args[0] else null) ?: return joinPoint.proceed()
        // 获取实体类
        val entityClass: KClass<BaseEntity> = try {
            val repo = AopContext.currentProxy() as BaseRepo<*>
            if (!repoEntityClassMap.containsKey(repo)) {
                synchronized(repo) {
                    if (!repoEntityClassMap.containsKey(repo)) {
                        val entity = repo.findAll(Pageable.ofSize(1)).content[0]
                        repoEntityClassMap[repo] = entity::class as KClass<BaseEntity>
                    }
                }
            }
            repoEntityClassMap[repo]!!
        } catch (_: Throwable) {
            return joinPoint.proceed()
        }
        // 使用缓存
        val signature = joinPoint.signature as MethodSignature
        when (signature.method.name) {
            "save" -> {
                val entity = joinPoint.proceed() as BaseEntity
                EntityHolder.deleteCache(entityClass, listOf(entity.id!!))
                return entity
            }

            "saveAll" -> {
                val entities = joinPoint.proceed() as List<*>
                EntityHolder.deleteCache(entityClass, entities.map { (it as BaseEntity).id!! })
                return entities
            }

            "findById" -> {
                val entities: List<BaseEntity> = EntityHolder.find(entityClass, listOf(arg as Int))
                if (entities.isNotEmpty()) {
                    return Optional.of(entities[0])
                }
                val optEntity = joinPoint.proceed() as Optional<*>
                if (optEntity.isPresent) {
                    EntityHolder.saveCache(entityClass, listOf(optEntity.get() as BaseEntity))
                }
                return optEntity
            }

            "findAllById" -> {
                val ids = (arg as Iterable<*>).map { it as Int }
                val entities = EntityHolder.find(entityClass, ids)
                val foundIds = entities.map { it.id }
                val missedIds = ids - foundIds.toSet()
                val missedEntities = (joinPoint.proceed(arrayOf<Any>(missedIds)) as List<*>).map { it as BaseEntity }
                EntityHolder.saveCache(entityClass, missedEntities)
                entities.addAll(missedEntities)
                // 若传入ids是列表，返回结果按传入id顺序排序
                entities.sortWith(Comparator.comparingInt { ids.indexOf(it.id) })
                return entities
            }

            "existsById" -> {
                val entity = (AopContext.currentProxy() as BaseRepo<*>).findById((arg as Int)).orElse(null)
                return entity != null
            }

            "deleteById" -> {
                EntityHolder.deleteCache(entityClass, listOf(arg as Int))
                return joinPoint.proceed()
            }

            "deleteAllById" -> {
                EntityHolder.deleteCache(entityClass, (arg as Iterable<*>).map { it as Int })
                return joinPoint.proceed()
            }

            "delete" -> {
                EntityHolder.deleteCache(entityClass, listOf((arg as BaseEntity).id!!))
                return joinPoint.proceed()
            }

            "deleteAll" -> {
                val deleteIds: MutableList<Int> = ArrayList()
                for (baseEntity in arg as Iterable<*>) {
                    deleteIds.add((baseEntity as BaseEntity).id!!)
                }
                EntityHolder.deleteCache(entityClass, deleteIds)
                return joinPoint.proceed()
            }
        }
        return joinPoint.proceed()
    }
}