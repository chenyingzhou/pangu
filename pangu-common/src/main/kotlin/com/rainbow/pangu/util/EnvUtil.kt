package com.rainbow.pangu.util

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
object EnvUtil : BeanFactoryAware {

    private lateinit var env: String

    val isProd: Boolean
        get() {
            return "prod" == env
        }
    val isTest: Boolean
        get() {
            return "test" == env
        }
    val isDev: Boolean
        get() {
            return "dev" == env
        }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        env = beanFactory.getBean(Environment::class.java).getProperty("env", "test").lowercase()
    }

}