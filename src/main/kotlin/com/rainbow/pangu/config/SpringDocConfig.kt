package com.rainbow.pangu.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val components = Components()
        val schemes = linkedMapOf(
            "token" to "token",
            "timestamp" to "unix_timestamp(s)",
            "sign" to "sign",
            "platform" to "platform(ios/android/h5/web)",
            "version" to "version",
        )
        schemes.forEach { (k, v) ->
            components.addSecuritySchemes(
                k, SecurityScheme().type(SecurityScheme.Type.APIKEY).`in`(SecurityScheme.In.HEADER).name(v)
            )
        }
        return OpenAPI().info(Info().title("彩虹桥C端").description("彩虹桥C端接口").version("V1.0"))
            .externalDocs(ExternalDocumentation().description("彩虹桥").url("https://www.rainbow-bridge.top"))
            .components(components)
            .addSecurityItem(SecurityRequirement().addList("token").addList("timestamp").addList("sign"))
    }

    @Bean
    fun groupedOpenApi(): GroupedOpenApi {
        return GroupedOpenApi.builder().group("彩虹桥").pathsToMatch("/**").build()
    }
}