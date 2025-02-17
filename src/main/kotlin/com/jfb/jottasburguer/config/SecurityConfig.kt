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

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        logger.info("Configurando SecurityFilterChain...")

        http
            .csrf { it.disable() } // Desabilita CSRF (não necessário para APIs stateless)
            .cors { it.configurationSource(corsConfigurationSource()) } // Configura CORS
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sessão stateless
            }
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // Tratamento de autenticação falha
                    .accessDeniedHandler(customAccessDeniedHandler()) // Tratamento de acesso negado
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll() // Permite acesso público a endpoints de autenticação
                    .requestMatchers(HttpMethod.GET, "/api/customers").permitAll() // Permite acesso público a listagem de clientes
                    .requestMatchers("/api/orders/**").authenticated()
                    .requestMatchers("/api/users").authenticated()
                    .requestMatchers("/api/products/**").authenticated() // Exige autenticação para acessar endpoints de produtos
                    .requestMatchers(
                        HttpMethod.GET,
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/api-docs/swagger-config"
                    ).permitAll() // Permite acesso ao Swagger e seus recursos
                    .anyRequest().authenticated() // Exige autenticação para qualquer outro endpoint
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // Adiciona o filtro JWT

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
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = allowedOrigins.toList() // Origens permitidas
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
        configuration.allowedHeaders = listOf("*") // Headers permitidos
        configuration.allowCredentials = true // Permite credenciais (cookies, headers de autenticação)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration) // Aplica a configuração a todos os endpoints
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