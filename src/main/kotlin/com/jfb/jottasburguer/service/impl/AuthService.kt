package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.exception.AuthenticationFailedException
import com.jfb.jottasburguer.model.dto.LoginRequest
import com.jfb.jottasburguer.model.dto.TokenResponse
import com.jfb.jottasburguer.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val authUserService: UserDetailsService,
    private val jwtService: JwtService
) {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    fun login(request: LoginRequest): TokenResponse {
        logger.info("Tentativa de login com email: ${request.email}")
        logger.info("Senha fornecida: ${request.password}")

        try {
            // Verifica se o usuário existe
            val user = authUserService.loadUserByUsername(request.email)
                ?: throw AuthenticationFailedException("Usuário não encontrado")

            // Tenta autenticar o usuário
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            logger.info("Autenticação bem-sucedida para o usuário: ${request.email}")

            val token = jwtService.generateToken(user)
            val refreshToken = jwtService.generateRefreshToken(user)

            logger.info("Token gerado: $token")
            logger.info("Refresh token gerado: $refreshToken")

            return TokenResponse(token, refreshToken)
        } catch (ex: UsernameNotFoundException) {
            logger.error("Usuário não encontrado: ${ex.message}")
            throw AuthenticationFailedException("Usuário não encontrado")
        } catch (ex: BadCredentialsException) {
            logger.error("Senha incorreta: ${ex.message}")
            throw AuthenticationFailedException("Senha incorreta")
        } catch (ex: Exception) {
            logger.error("Falha na autenticação: ${ex.message}")
            throw AuthenticationFailedException("Falha na autenticação: ${ex.message}")
        }
    }

    fun refreshToken(request: TokenResponse): TokenResponse {
        logger.info("Tentativa de refresh token com refresh token: ${request.refreshToken}")
        val username = jwtService.extractUsername(request.refreshToken)
        logger.info("Username extraído do refresh token: $username")

        val user = authUserService.loadUserByUsername(username)
        if (jwtService.isTokenValid(request.refreshToken, user)) {
            val newToken = jwtService.generateToken(user)
            logger.info("Novo token gerado: $newToken")
            return TokenResponse(newToken, request.refreshToken)
        }

        logger.error("Refresh token inválido para o usuário: $username")
        throw RuntimeException("Refresh token inválido")
    }
}