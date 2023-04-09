package com.rainbow.pangu.util

import org.springframework.core.env.Environment

object EnvUtil {

    private val env by lazy { BeanUtil.getBean(Environment::class).getProperty("env", "test").lowercase() }

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
}