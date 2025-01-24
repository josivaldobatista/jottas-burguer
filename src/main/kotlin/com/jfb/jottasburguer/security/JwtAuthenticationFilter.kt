package com.jfb.jottasburguer.security

import com.jfb.jottasburguer.service.UserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.slf4j.LoggerFactory

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val authUserService: UserDetailsService
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/refresh-token")) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
        logger.info("Authorization header: $authHeader")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            logger.info("Token JWT recebido: $token")

            val username = jwtService.extractUsername(token)
            logger.info("Username extraído do token: $username")

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                logger.info("Tentando carregar UserDetails para o username: $username")
                val userDetails = authUserService.loadUserByUsername(username)

                if (jwtService.isTokenValid(token, userDetails)) {
                    logger.info("Token válido para o usuário: ${userDetails.username}")
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                } else {
                    logger.error("Token inválido para o usuário: ${userDetails.username}")
                }
            }
        } else {
            logger.warn("Authorization header não encontrado ou inválido")
        }

        filterChain.doFilter(request, response)
    }
}