package com.rainbow.pangu.util

import com.rainbow.pangu.util.JacksonUtil.toJson
import com.rainbow.pangu.util.JacksonUtil.toObject
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.core.DefaultTypedTuple
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations.TypedTuple
import org.springframework.data.redis.core.types.Expiration
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@Component
object RedisUtil : BeanFactoryAware {

    private lateinit var redisTemplate: StringRedisTemplate

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        redisTemplate = beanFactory.getBean(StringRedisTemplate::class.java)
    }

    /**
     * 保存键值对，最终过期时间为 expireTime + rand(-randTime, randTime)
     *
     * @param pairs      k -> v
     * @param expireTime 过期时间
     * @param randTime   浮动时间
     */
    fun store(pairs: Map<String, Any>, expireTime: Long = 86400 * 30, randTime: Long = 0) {
        val validRandTime = if (randTime > expireTime / 2) expireTime / 2 else randTime
        val map: MutableMap<String, String> = HashMap()
        pairs.forEach { (k, v) -> map[k] = toJson(v) }
        redisTemplate.executePipelined { connection: RedisConnection ->
            for ((k, v) in map) {
                val finalRandTime = if (validRandTime > 0) Random().nextLong(validRandTime * 2) - validRandTime else 0
                val finalTime = expireTime + finalRandTime
                if (finalTime <= 0) {
                    connection.set(k.toByteArray(), v.toByteArray())
                } else {
                    connection.set(
                        k.toByteArray(),
                        v.toByteArray(),
                        Expiration.from(finalTime, TimeUnit.SECONDS),
                        RedisStringCommands.SetOption.UPSERT
                    )
                }
            }
            null
        }
    }

    /**
     * 保存键值对
     */
    fun store(pair: Pair<String, Any>, expireTime: Long = 86400 * 30, randTime: Long = 0) {
        store(mapOf(pair.first to pair.second), expireTime, randTime)
    }

    fun del(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    fun del(keys: Collection<String>) {
        redisTemplate.delete(keys)
    }

    fun increment(key: String): Long? {
        return redisTemplate.opsForValue().increment(key)
    }

    fun expire(key: String, expireTime: Long) {
        redisTemplate.expire(key, expireTime, TimeUnit.SECONDS)
    }

    fun getExpire(key: String): Long {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS)
    }

    fun <E : Any> getSingle(key: String, cls: KClass<E>): E? {
        val json = redisTemplate.opsForValue().get(key)
        return if (json != null) toObject(json, cls) else null
    }

    fun <E : Any> getMulti(keys: Collection<String?>, cls: KClass<E>): List<E> {
        val objs: MutableList<E> = ArrayList()
        if (CollectionUtils.isEmpty(keys)) {
            return objs
        }
        val jsons = redisTemplate.opsForValue().multiGet(keys)!!
        jsons.removeIf { it == null || it.isBlank() }
        for (json in jsons) {
            val obj = toObject(json, cls)
            if (obj != null) {
                objs.add(obj as E)
            }
        }
        return objs
    }

    /**
     * 按score **从大到小** 排行
     *
     * @param key      key
     * @param page     页码，若page <= 0，则视为不分页，将返回所有数据
     * @param pageSize 每页数量
     * @return member->score
     */
    fun rankDesc(key: String, page: Int, pageSize: Int): Map<String, Double> {
        val start = if (page > 0) (page - 1L) * pageSize else 0
        val end = if (page > 0) start + pageSize - 1 else -1
        val scoreSet = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end) ?: setOf()
        val scoreMap: MutableMap<String, Double> = HashMap()
        scoreSet.forEach { scoreMap[it.value!!] = it.score ?: 0.0 }
        return scoreMap
    }

    /**
     * 按score **从小到大** 排行
     *
     * @param key      key
     * @param page     页码，若page <= 0，则视为不分页，将返回所有数据
     * @param pageSize 每页数量
     * @return member->score
     */
    fun rankAsc(key: String, page: Int, pageSize: Int): Map<String, Double> {
        val start = if (page > 0) (page - 1L) * pageSize else 0
        val end = if (page > 0) start + pageSize - 1 else -1
        val scoreSet = redisTemplate.opsForZSet().rangeWithScores(key, start, end) ?: setOf()
        val scoreMap: MutableMap<String, Double> = HashMap()
        scoreSet.forEach { scoreMap[it.value!!] = it.score ?: 0.0 }
        return scoreMap
    }

    /**
     * 返回有序集合元素对应的score
     *
     * @param key     有序集合key
     * @param members 元素列表
     * @param <E>     推荐使用Int|Long|String，不可使用复杂对象
     * @return member->score
    </E> */
    fun <E> getScores(key: String, members: Iterable<E>): Map<E, Double> {
        val scoreMap: MutableMap<E, Double> = HashMap()
        val memberList = members.toList()
        val stringMemberList = memberList.map { it.toString() }
        val scores = redisTemplate.opsForZSet().score(key, *stringMemberList.toTypedArray()) ?: return scoreMap
        for (i in scores.indices) {
            scoreMap[memberList[i]] = if (scores[i] != null) scores[i] else 0.0
        }
        return scoreMap
    }

    /**
     * 向有序集合写入元素及分数(zAdd命令添加元素)
     *
     * @param key      有序集合key
     * @param scoreMap member->score
     * @param <E>      推荐使用Int|Long|String，不可使用复杂对象
    </E> */
    fun <E> setScores(key: String, scoreMap: Map<E, Number>) {
        val tuples: MutableSet<TypedTuple<String>> = HashSet()
        scoreMap.forEach { (k, v) -> tuples.add(DefaultTypedTuple(k.toString(), v.toDouble())) }
        if (tuples.size > 0) {
            redisTemplate.opsForZSet().add(key, tuples)
        }
    }

    /**
     * 集合元素数量
     */
    fun sCard(key: String): Int {
        val size = redisTemplate.opsForSet().size(key)
        return size?.toInt() ?: 0
    }

    /**
     * 获取集合元素
     */
    fun <E : Any> sPop(key: String, count: Int, cls: KClass<E>): List<E?> {
        val members = redisTemplate.opsForSet().pop(key, count.toLong())!!
        return members.asSequence().map { toObject(it, cls) }.filter { it != null }.toList()
    }

    /**
     * 向集合添加元素
     */
    fun sAdd(key: String, members: Iterable<Any>) {
        val stringMemberList = members.map { toJson(it) }
        if (stringMemberList.isNotEmpty()) {
            redisTemplate.opsForSet().add(key, *stringMemberList.toTypedArray())
        }
    }
}
