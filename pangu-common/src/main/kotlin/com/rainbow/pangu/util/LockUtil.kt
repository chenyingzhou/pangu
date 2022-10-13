package com.rainbow.pangu.util

import com.rainbow.pangu.exception.BizException
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
object LockUtil : BeanFactoryAware {

    private lateinit var redisTemplate: StringRedisTemplate
    private const val prefix = "lock:"
    private const val defaultMsg = "操作太频繁"
    private const val defaultExpire = 10

    @Throws(BeansException::class)
    override fun setBeanFactory(beanFactory: BeanFactory) {
        redisTemplate = beanFactory.getBean(StringRedisTemplate::class.java)
    }

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