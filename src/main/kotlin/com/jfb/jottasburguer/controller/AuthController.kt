package com.jfb.jottasburguer.controller

import com.jfb.jottasburguer.model.dto.LoginRequest
import com.jfb.jottasburguer.model.dto.TokenResponse
import com.jfb.jottasburguer.security.JwtService
import com.jfb.jottasburguer.service.AuthUserService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val authUserService: AuthUserService,
    private val jwtService: JwtService
) {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        logger.info("Tentativa de login com email: ${request.email}")
        logger.info("Senha fornecida: ${request.password}") // Log da senha fornecida

        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            logger.info("Autenticação bem-sucedida para o usuário: ${request.email}")

            val userDetails = authentication.principal as UserDetails
            logger.info("UserDetails carregado: ${userDetails.username}, roles: ${userDetails.authorities}")

            val token = jwtService.generateToken(userDetails)
            val refreshToken = jwtService.generateRefreshToken(userDetails)

            logger.info("Token gerado: $token")
            logger.info("Refresh token gerado: $refreshToken")

            return ResponseEntity.ok(TokenResponse(token, refreshToken))
        } catch (ex: Exception) {
            logger.error("Falha na autenticação: ${ex.message}")
            throw RuntimeException("Falha na autenticação: ${ex.message}")
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: TokenResponse): ResponseEntity<TokenResponse> {
        logger.info("Tentativa de refresh token com refresh token: ${request.refreshToken}")
        val username = jwtService.extractUsername(request.refreshToken)
        logger.info("Username extraído do refresh token: $username")

        val user = authUserService.loadUserByUsername(username)
        if (jwtService.isTokenValid(request.refreshToken, user)) {
            val newToken = jwtService.generateToken(user)
            logger.info("Novo token gerado: $newToken")
            return ResponseEntity.ok(TokenResponse(newToken, request.refreshToken))
        }

        logger.error("Refresh token inválido para o usuário: $username")
        throw RuntimeException("Refresh token inválido")
    }
}