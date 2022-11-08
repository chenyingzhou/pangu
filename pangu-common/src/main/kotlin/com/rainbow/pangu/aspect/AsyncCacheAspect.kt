package com.rainbow.pangu.aspect

import com.rainbow.pangu.annotation.AsyncCache
import com.rainbow.pangu.util.EnvUtil
import com.rainbow.pangu.util.HexUtil.toHex
import com.rainbow.pangu.util.JacksonUtil.toObject
import com.rainbow.pangu.util.RedisUtil
import com.rainbow.pangu.util.RedisUtil.expire
import com.rainbow.pangu.util.RedisUtil.getExpire
import com.rainbow.pangu.util.RedisUtil.getSingle
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture

@Aspect
@Component
class AsyncCacheAspect {
    @Around("@annotation(asyncCache)")
    @Throws(Throwable::class)
    fun cacheAround(joinPoint: ProceedingJoinPoint, asyncCache: AsyncCache): Any? {
        if (!EnvUtil.isProd) {
            return joinPoint.proceed()
        }
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val sb = StringBuilder()
        for (o in joinPoint.args) {
            sb.append(o).append(":")
        }
        val paramHex = toHex(sb.toString())
        val cacheKey = "ASYNC_CACHE:$className:$methodName:$paramHex"
        val timeout = asyncCache.timeout
        var cacheResult = getSingle(cacheKey, String::class)
        var result: Any? = null
        if (cacheResult == null) {
            val lockObject = SyncLockHelper.storeObject(cacheKey)
            // 防止缓存雪崩，由于相同的cacheKey并不是同一个对象，所以引入SyncLockHelper存储锁对象，保证相同cacheKey获取到同一个锁对象
            synchronized(lockObject) {
                cacheResult = getSingle(cacheKey, String::class)
                if (cacheResult == null) {
                    result = joinPoint.proceed()
                    RedisUtil.store(cacheKey to result!!, timeout.toLong())
                }
            }
        } else {
            val ttl = getExpire(cacheKey)
            if (ttl <= timeout / 2) {
                // 当缓存时间过半时，异步刷新缓存，并同时立即更新缓存时间，防止多个线程做无用功
                expire(cacheKey, timeout.toLong())
                CompletableFuture.runAsync {
                    try {
                        val newResult = joinPoint.proceed()
                        RedisUtil.store(cacheKey to newResult!!, timeout.toLong())
                    } catch (ignored: Throwable) {
                    }
                }
            }
        }
        if (result == null) {
            val signature = joinPoint.signature as MethodSignature
            result = toObject(cacheResult!!, signature.method.genericReturnType)
        }
        return result
    }

    /**
     * 生成并保存锁对象，并使用LRU算法淘汰
     */
    object SyncLockHelper {
        /**
         * 用于通过字符串获取锁对象
         */
        private val objectMap: MutableMap<Any, Any> = HashMap()

        /**
         * LRU链表
         */
        private val objects: Queue<Any> = LinkedList()

        /**
         * LRU链表各对象计数器
         */
        private val objectCounts: MutableMap<Any, Int> = HashMap()

        /**
         * 锁对象数量限制
         */
        private const val maxSize = 1000

        /**
         * 通过KEY生成并存储锁对象
         */
        @Synchronized
        fun storeObject(key: Any): Any {
            // 使用原来已存在的key
            val existsKey = objectMap[key] ?: key
            // 开始记录
            objectMap[existsKey] = existsKey
            objects.offer(existsKey)
            objectCounts[existsKey] = objectCounts.getOrDefault(existsKey, 0) + 1
            // 剔除过旧的对象
            while (objects.size > maxSize) {
                val obj = objects.poll()
                var objCount = objectCounts.getOrDefault(obj, 1)
                objCount--
                if (objCount == 0) {
                    objectCounts.remove(obj)
                    objectMap.remove(obj)
                } else {
                    objectCounts[obj] = objCount
                }
            }
            return existsKey
        }
    }
}