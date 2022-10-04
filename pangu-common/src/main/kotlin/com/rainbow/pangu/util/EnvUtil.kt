package com.rainbow.pangu.util

object EnvUtil {
    private val env = System.getProperties().getProperty("env", "test").lowercase()

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