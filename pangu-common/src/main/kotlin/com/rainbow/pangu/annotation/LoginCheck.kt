package com.rainbow.pangu.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoginCheck(
    /**
     * 签名校验
     */
    val checkSign: Boolean = false,
    /**
     * 对用户操作加锁
     */
    val lock: Boolean = false
)