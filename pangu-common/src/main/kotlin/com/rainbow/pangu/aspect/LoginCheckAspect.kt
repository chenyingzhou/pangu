package com.rainbow.pangu.aspect

import com.rainbow.pangu.annotation.LoginCheck
import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.exception.BizException
import com.rainbow.pangu.exception.BizExceptionEnum
import com.rainbow.pangu.threadholder.ClientInfoHolder
import com.rainbow.pangu.util.EnvUtil
import com.rainbow.pangu.util.LockUtil
import com.rainbow.pangu.util.RedisUtil
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LoginCheckAspect {
    @Around("@annotation(loginCheck)")
    @Throws(Throwable::class)
    fun loginCheck(joinPoint: ProceedingJoinPoint, loginCheck: LoginCheck): Any? {
        val userId = ClientInfoHolder.userId
        // 校验登录情况
        if (userId == 0) {
            throw BizException(BizExceptionEnum.UNAUTHORIZED, "登录信息已过期")
        }
        // 校验签名(仅正式环境)，校验不通过的，使其退出登录
        if (EnvUtil.isProd && loginCheck.checkSign && !ClientInfoHolder.checkSign()) {
            RedisUtil.del(KeyTemplate.USER_TOKEN.fill(ClientInfoHolder.token))
            throw BizException(BizExceptionEnum.UNAUTHORIZED, "登录信息已过期")
        }
        // 接口对当前用户加锁，防止同一用户并发请求
        val lockKey = joinPoint.signature.name + ":" + userId
        if (loginCheck.lock) {
            LockUtil.lockOrThrow(lockKey, 60)
        }
        // TOKEN续期
        RedisUtil.store(KeyTemplate.USER_TOKEN.fill(ClientInfoHolder.token) to userId, 86400 * 7)
        return try {
            joinPoint.proceed()
        } finally {
            if (loginCheck.lock) {
                LockUtil.unlock(lockKey)
            }
        }
    }
}