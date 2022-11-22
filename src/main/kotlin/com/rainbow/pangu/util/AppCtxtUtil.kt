package com.rainbow.pangu.util

import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

object AppCtxtUtil {

    private lateinit var applicationContext: ApplicationContext

    fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    fun <T> getBean(clazz: Class<T>): T {
        return applicationContext.getBean(clazz)
    }

    fun <T : Any> getBean(clazz: KClass<T>): T {
        return applicationContext.getBean(clazz.java)
    }

}