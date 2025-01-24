package com.jfb.jottasburguer.security

import com.jfb.jottasburguer.service.UserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val authUserService: UserDetailsService
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
        private val PUBLIC_ENDPOINTS = listOf("/api/auth/login", "/api/auth/refresh-token")
        private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath

        // Ignora endpoints públicos
        if (PUBLIC_ENDPOINTS.any { path.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val authHeader = request.getHeader("Authorization")
            logger.info("Authorization header recebido para o endpoint: $path")

            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                val token = authHeader.substring(BEARER_PREFIX.length)
                logger.info("Token JWT recebido para o endpoint: $path")

                if (token.isNotBlank()) {
                    authenticateUserFromToken(token, request)
                } else {
                    logger.warn("Token JWT está vazio ou nulo para o endpoint: $path")
                }
            } else {
                logger.warn("Authorization header não encontrado ou inválido para o endpoint: $path")
            }
        } catch (ex: Exception) {
            logger.error("Erro inesperado durante a autenticação JWT para o endpoint: $path", ex)
        }

        filterChain.doFilter(request, response)
    }

    private fun authenticateUserFromToken(token: String, request: HttpServletRequest) {
        try {
            val username = jwtService.extractUsername(token)
            logger.info("Username extraído do token: $username")

            if (SecurityContextHolder.getContext().authentication == null) {
                logger.info("Tentando carregar UserDetails para o username: $username")
                val userDetails = authUserService.loadUserByUsername(username)

                if (jwtService.isTokenValid(token, userDetails)) {
                    logger.info("Token válido para o usuário: ${userDetails.username}")
                    logger.info("Authorities do usuário: ${userDetails.authorities}") // Log das roles
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                } else {
                    logger.error("Token inválido para o usuário: ${userDetails.username}")
                }
            }
        } catch (ex: Exception) {
            logger.error("Erro ao autenticar usuário a partir do token JWT", ex)
        }
    }
}