package com.jfb.jottasburguer.config

import com.jfb.jottasburguer.security.JwtAuthenticationFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    private val logger = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Value("\${app.security.allowed-origins}")
    private lateinit var allowedOrigins: Array<String>

    companion object {
        private val PUBLIC_ENDPOINTS = arrayOf(
            "/api/auth/**",
            "/api/customers"
        )

        private val SWAGGER_ENDPOINTS = arrayOf(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api-docs/swagger-config"
        )

        private val AUTHENTICATED_ENDPOINTS = arrayOf(
            "/api/orders/**",
            "/api/users",
            "/api/products/**"
        )
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        logger.info("Configurando SecurityFilterChain...")

        http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler(customAccessDeniedHandler())
            }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(*PUBLIC_ENDPOINTS).permitAll()
                    .requestMatchers(*SWAGGER_ENDPOINTS).permitAll() // Exige autenticação para o Swagger
                    .requestMatchers(*AUTHENTICATED_ENDPOINTS).authenticated() // Exige autenticação para APIs protegidas
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        logger.info("Configurando BCryptPasswordEncoder...")
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        logger.info("Configurando AuthenticationManager...")
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        logger.info("Configurando CORS...")
        val configuration = CorsConfiguration().apply {
            allowedOrigins = this@SecurityConfig.allowedOrigins.toList()
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun customAccessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { request, response, accessDeniedException ->
            logger.warn("Acesso negado para o endpoint: ${request.requestURI}", accessDeniedException)
            response.sendError(HttpStatus.FORBIDDEN.value(), "Acesso negado: Você não tem permissão para acessar este recurso.")
        }
    }
}
