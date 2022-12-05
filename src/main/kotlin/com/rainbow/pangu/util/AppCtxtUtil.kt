package com.rainbow.pangu.util

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
object AppCtxtUtil : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    fun <T : Any> getBean(clazz: KClass<T>): T {
        return applicationContext.getBean(clazz.java)
    }

}