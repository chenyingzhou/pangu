package com.rainbow.pangu.api.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "oss")
class OssConfig {
    var endpoint = ""
    var accessKeyId = ""
    var accessKeySecret = ""
    var bucketName = ""
}