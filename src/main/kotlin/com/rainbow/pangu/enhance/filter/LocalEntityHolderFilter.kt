package com.rainbow.pangu.enhance.filter

import com.rainbow.pangu.enhance.threadholder.EntityHolder
import org.springframework.stereotype.Component
import java.io.IOException
import jakarta.servlet.*

@Component
class LocalEntityHolderFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        try {
            EntityHolder.enable()
            filterChain.doFilter(request, response)
        } finally {
            EntityHolder.disable()
        }
    }
}