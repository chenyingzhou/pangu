package com.rainbow.pangu.enhance.filter

import org.springframework.stereotype.Component
import java.io.IOException
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class CorsFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val originHost = httpRequest.getHeader("Origin") ?: "*"
        httpResponse.addHeader("Access-Control-Allow-Origin", originHost)
        httpResponse.addHeader("Access-Control-Allow-Credentials", "true")
        httpResponse.addHeader("Access-Control-Allow-Methods", "OPTIONS,GET,POST,PUT,DELETE")
        httpResponse.addHeader("Access-Control-Allow-Headers", "*")
        httpResponse.addHeader("Access-Control-Max-Age", "86400")
        filterChain.doFilter(request, response)
    }
}