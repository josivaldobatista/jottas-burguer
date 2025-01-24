package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.exception.AuthenticationFailedException
import com.jfb.jottasburguer.exception.InvalidTokenException
import com.jfb.jottasburguer.model.dto.LoginRequest
import com.jfb.jottasburguer.model.dto.TokenResponse
import com.jfb.jottasburguer.security.JwtService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val authUserService: UserDetailsService,
    private val jwtService: JwtService
) : AuthService {

    private val logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)

    override fun login(@Valid request: LoginRequest): TokenResponse {
        logger.info("Tentativa de login com email: ${request.email}")

        try {
            // Autentica o usuário
            authenticateUser(request.email, request.password)

            // Carrega o usuário e gera os tokens
            val user = authUserService.loadUserByUsername(request.email)
            val token = jwtService.generateToken(user)
            val refreshToken = jwtService.generateRefreshToken(user)

            logger.info("Token gerado com sucesso para o usuário: ${request.email}")
            return TokenResponse(token, refreshToken)
        } catch (ex: UsernameNotFoundException) {
            logger.error("Usuário não encontrado: ${ex.message}")
            throw AuthenticationFailedException("Usuário não encontrado")
        } catch (ex: BadCredentialsException) {
            logger.error("Senha incorreta para o usuário: ${request.email}")
            throw AuthenticationFailedException("Senha incorreta")
        } catch (ex: Exception) {
            logger.error("Erro inesperado durante a autenticação: ${ex.message}")
            throw ex // Relança a exceção original
        }
    }

    override fun refreshToken(@Valid request: TokenResponse): TokenResponse {
        logger.info("Tentativa de refresh token com refresh token: [REDACTED]")

        val username = jwtService.extractUsername(request.refreshToken)
        logger.info("Username extraído do refresh token: $username")

        val user = authUserService.loadUserByUsername(username)
        if (jwtService.isTokenValid(request.refreshToken, user)) {
            val newToken = jwtService.generateToken(user)
            logger.info("Novo token gerado com sucesso para o usuário: $username")
            return TokenResponse(newToken, request.refreshToken)
        }

        logger.error("Refresh token inválido para o usuário: $username")
        throw InvalidTokenException("Refresh token inválido")
    }

    private fun authenticateUser(email: String, password: String) {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, password)
            )
            logger.info("Autenticação bem-sucedida para o usuário: $email")
        } catch (ex: BadCredentialsException) {
            logger.error("Falha na autenticação para o usuário: $email")
            throw ex
        }
    }
}