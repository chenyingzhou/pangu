package com.rainbow.pangu.util

import jakarta.servlet.http.HttpServletRequest

object IpUtil {
    private const val proxyRealIpAuth = "2GxmrmLBTyE6QllFIxOXwfgTLv17Hi3d"

    /**
     * 获取客户端的IP，若该请求通过反向代理，以反向代理的设置X-Real-IP为准，若不经过，以客户端的最后一层正向代理为准
     */
    fun getIp(request: HttpServletRequest): String {
        val remoteIp = request.remoteAddr
        val realIp = request.getHeader("X-Real-IP")
        val realIpAuth = request.getHeader("X-Real-IP-Auth")
        return if (proxyRealIpAuth == realIpAuth) realIp else remoteIp
    }
}