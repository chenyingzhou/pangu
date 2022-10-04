package com.rainbow.pangu.util

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class SpringContextUtil : ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        Companion.applicationContext = applicationContext
    }

    companion object {
        private lateinit var applicationContext: ApplicationContext
        fun <T> getBean(clazz: Class<T>): T {
            return applicationContext.getBean(clazz)
        }

        fun <T : Any> getBean(clazz: KClass<T>): T {
            return applicationContext.getBean(clazz.java)
        }
    }
}