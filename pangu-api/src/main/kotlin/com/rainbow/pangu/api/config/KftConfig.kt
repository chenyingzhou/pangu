package com.rainbow.pangu.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "kft")
class KftConfig {
    var keyStorePassword = ""
    var keyPassword = ""
    var clientIp = ""

    // 主动支付编号
    var proactivePayAccount = ""
    var proactivePayPassword = ""

    // 查询账户编号
    var queryAccount = ""
}