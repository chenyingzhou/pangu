package com.rainbow.pangu.util

import com.rainbow.pangu.exception.BizException
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit

object LockUtil {
    private val redisTemplate by lazy { AppCtxtUtil.getBean(StringRedisTemplate::class) }
    private const val prefix = "lock:"
    private const val defaultMsg = "操作太频繁"
    private const val defaultExpire = 10

    fun lock(key: String, expire: Int = defaultExpire): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(prefix + key, "1", expire.toLong(), TimeUnit.SECONDS) == true
    }

    fun lockOrThrow(key: String, expire: Int = defaultExpire, msg: String = defaultMsg) {
        if (!lock(key, expire)) {
            throw BizException(msg)
        }
    }

    fun unlock(key: String) {
        redisTemplate.delete(prefix + key)
    }
}