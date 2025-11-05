package com.wafflestudio.spring2025.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val jwtSchemeName = "jwtAuth"

        val info = Info()
            .title("Timetable API")
            .description("2025 springboot assignment-2")
            .version("v1.0.0")

        val securityScheme = SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")

        val securityRequirement = SecurityRequirement().addList(jwtSchemeName)

        return OpenAPI()
            .components(Components().addSecuritySchemes(jwtSchemeName, securityScheme))
            .info(info)
            .addSecurityItem(securityRequirement)
    }
}