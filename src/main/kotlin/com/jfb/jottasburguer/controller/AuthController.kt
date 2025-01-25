package com.jfb.jottasburguer.controller

import com.jfb.jottasburguer.model.dto.LoginRequest
import com.jfb.jottasburguer.model.dto.TokenResponse
import com.jfb.jottasburguer.service.AuthServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthServiceImpl
) {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        logger.info("Tentativa de login com email: ${request.email}")
        val tokenResponse = authService.login(request)
        return ResponseEntity.ok(tokenResponse)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody request: TokenResponse): ResponseEntity<TokenResponse> {
        logger.info("Tentativa de refresh token com refresh token: ${request.refreshToken}")
        val tokenResponse = authService.refreshToken(request)
        return ResponseEntity.ok(tokenResponse)
    }
}