package com.rainbow.pangu

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EntityScan("com.rainbow.pangu.entity")
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}