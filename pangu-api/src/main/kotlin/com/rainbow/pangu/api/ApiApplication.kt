package com.rainbow.pangu.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.rainbow.pangu")
@EntityScan("com.rainbow.pangu.entity")
@EnableJpaRepositories(basePackages = ["com.rainbow.pangu.repository"])
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}