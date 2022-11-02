package com.rainbow.pangu.threadholder

import com.rainbow.pangu.constant.KeyTemplate
import com.rainbow.pangu.constant.Platform
import com.rainbow.pangu.util.RedisUtil.getSingle
import org.springframework.util.DigestUtils
import java.nio.charset.StandardCharsets

object ClientInfoHolder {
    private const val SALT = "pITfPb359onJCpuPxRwmLZ0YlzT2viSv"
    private val client = ThreadLocal.withInitial { ClientInfo() }

    fun setClientInfo(
        token: String,
        timestamp: Long,
        sign: String,
        ip: String,
        platform: Platform,
        version: Int,
        userId: Int
    ) {
        val clientInfo = client.get()
        clientInfo.token = token
        clientInfo.timestamp = timestamp
        clientInfo.sign = sign
        clientInfo.ip = ip
        clientInfo.platform = platform
        clientInfo.version = version
        clientInfo.userId = userId
    }

    fun clean() {
        client.get().clean()
    }

    val userId: Int
        get() {
            val clientInfo = client.get()
            if (clientInfo.userId == 0 && clientInfo.token != "") {
                try {
                    clientInfo.userId = getSingle(KeyTemplate.USER_TOKEN.fill(token), Int::class) ?: 0
                } catch (ignored: Throwable) {
                }
            }
            return clientInfo.userId
        }

    val token: String
        get() = client.get().token

    val ip: String
        get() = client.get().ip

    val platform: Platform
        get() = client.get().platform

    val version: Int
        get() = client.get().version

    fun checkSign(): Boolean {
        if (userId == 0) {
            return false
        }
        val clientInfo = client.get()
        // 校验时间
        val now = System.currentTimeMillis() / 1000
        val timeDelta = now - clientInfo.timestamp
        if (timeDelta > 60 || timeDelta < -60) {
            return false
        }
        // 校验签名
        val str = clientInfo.token + clientInfo.timestamp + SALT
        val hex = DigestUtils.md5DigestAsHex(str.toByteArray(StandardCharsets.UTF_8))
        return clientInfo.sign == hex
    }

    private class ClientInfo {
        var token: String = ""
        var timestamp: Long = 0
        var sign: String = ""
        var userId = 0
        var ip: String = ""
        var platform: Platform = Platform.H5
        var version = 0

        fun clean() {
            token = ""
            timestamp = 0
            sign = ""
            userId = 0
            ip = ""
            platform = Platform.H5
            version = 0
        }
    }
}
