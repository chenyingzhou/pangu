package com.rainbow.pangu.enhance.filter

import com.rainbow.pangu.constant.Platform
import com.rainbow.pangu.enhance.threadholder.ClientInfoHolder
import com.rainbow.pangu.util.EnvUtil
import com.rainbow.pangu.util.IpUtil
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

@Component
class LocalClientHolderFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        try {
            val httpRequest = request as HttpServletRequest
            val ip = IpUtil.getIp(httpRequest)
            val token = httpRequest.getHeader("token") ?: ""
            val sign = httpRequest.getHeader("sign") ?: ""
            val timestampStr = httpRequest.getHeader("timestamp") ?: ""
            val platformStr = httpRequest.getHeader("platform") ?: ""
            val versionStr = httpRequest.getHeader("version") ?: ""
            val userIdStr = httpRequest.getHeader("userId") ?: httpRequest.getParameter("userId") ?: "0"
            var timestamp: Long = 0
            try {
                timestamp = timestampStr.toLong()
            } catch (_: Throwable) {
            }
            var userId = 0
            try {
                if (!EnvUtil.isProd) userId = userIdStr.toInt()
            } catch (_: Throwable) {
            }
            var version = 0
            try {
                val versionParts = versionStr.split(".")
                version = versionParts[0].toInt() * 1000000 + versionParts[1].toInt() * 1000 + versionParts[2].toInt()
            } catch (ignored: Throwable) {
            }
            val platform = when (platformStr) {
                "ios" -> Platform.IOS
                "android" -> Platform.ANDROID
                "web" -> Platform.WEB
                else -> Platform.H5
            }
            ClientInfoHolder.setClientInfo(token, timestamp, sign, ip, platform, version, userId)
            filterChain.doFilter(request, response)
        } finally {
            ClientInfoHolder.clean()
        }
    }
}