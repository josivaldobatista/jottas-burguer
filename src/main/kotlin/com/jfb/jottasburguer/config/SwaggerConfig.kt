package com.jfb.jottasburguer.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Jottas Burguer API")
                    .version("1.0")
                    .description("API para gerenciamento de usuários e pedidos do Jottas Burguer.")
            )
    }
}