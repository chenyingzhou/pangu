package com.rainbow.pangu.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScheduleLocker(
    /**
     * 分布式任务锁定时间(s)
     */
    val timeout: Int = 3600,
    /**
     * 执行完是否解锁
     */
    val unlock: Boolean = true,
    /**
     * 是否在开发环境运行
     */
    val enableOnDev: Boolean = false,
)