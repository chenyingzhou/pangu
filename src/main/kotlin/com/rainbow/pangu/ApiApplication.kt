package com.rainbow.pangu

import com.rainbow.pangu.util.AppCtxtUtil
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EntityScan("com.rainbow.pangu.entity")
@EnableJpaRepositories(basePackages = ["com.rainbow.pangu.repository"])
class ApiApplication

fun main(args: Array<String>) {
    val applicationContext = runApplication<ApiApplication>(*args)
    AppCtxtUtil.setApplicationContext(applicationContext)
}