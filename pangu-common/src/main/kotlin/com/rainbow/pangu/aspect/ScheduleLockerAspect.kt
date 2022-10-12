package com.rainbow.pangu.aspect

import com.rainbow.pangu.annotation.ScheduleLocker
import com.rainbow.pangu.util.EnvUtil
import com.rainbow.pangu.util.LockUtil
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class ScheduleLockerAspect {
    @Around("@annotation(scheduleLocker)")
    @Throws(Throwable::class)
    fun taskLockerAround(joinPoint: ProceedingJoinPoint, scheduleLocker: ScheduleLocker): Any? {
        // 判断是否在开发环境运行
        if (EnvUtil.isDev && !scheduleLocker.enableOnDev) {
            return null
        }
        // 加锁
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val timeout = scheduleLocker.timeout
        val lockKey = "TASK_LOCKER:$className:$methodName"
        val locked = LockUtil.lock(lockKey, timeout)
        return if (!locked) {
            null
        } else try {
            joinPoint.proceed()
        } finally {
            if (scheduleLocker.unlock) {
                LockUtil.unlock(lockKey)
            }
        }
    }
}