package com.rainbow.pangu.enhance.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AsyncCache(val timeout: Int = 60)