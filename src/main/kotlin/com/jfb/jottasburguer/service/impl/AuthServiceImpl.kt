package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.AuthenticationFailedException
import com.jfb.jottasburguer.exception.InvalidTokenException
import com.jfb.jottasburguer.exception.UserNotFoundException
import com.jfb.jottasburguer.model.dto.LoginRequest
import com.jfb.jottasburguer.model.dto.TokenResponse
import com.jfb.jottasburguer.repository.UserRepository
import com.jfb.jottasburguer.security.JwtService
import com.jfb.jottasburguer.service.AuthService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val authUserService: UserDetailsService,
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) : AuthService {

    private val logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)

    override fun login(@Valid request: LoginRequest): TokenResponse {
        logger.info("Tentativa de login com email: ${request.email}")

        // Verifica se o email existe e autentica o usuário
        val user = authenticateAndLoadUser(request.email, request.password)

        // Gera os tokens
        return generateTokensForUser(user)
    }

    override fun refreshToken(@Valid request: TokenResponse): TokenResponse {
        logger.info("Tentativa de refresh token com refresh token: [REDACTED]")

        val username = jwtService.extractUsername(request.refreshToken)
        logger.info("Username extraído do refresh token: $username")

        val user = authUserService.loadUserByUsername(username)
        if (!jwtService.isTokenValid(request.refreshToken, user)) {
            logger.error("Refresh token inválido para o usuário: $username")
            throw InvalidTokenException("Refresh token inválido")
        }

        // Gera um novo token
        return generateTokensForUser(user)
    }

    private fun authenticateAndLoadUser(email: String, password: String): UserDetails {
        try {
            // Verifica se o email existe
            if (!userRepository.existsByEmail(email)) {
                logger.error("Email não encontrado: $email")
                throw UserNotFoundException("Email não encontrado: $email")
            }

            // Autentica o usuário
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
            logger.info("Autenticação bem-sucedida para o usuário: $email")

            // Carrega o usuário
            return authUserService.loadUserByUsername(email)
        } catch (ex: BadCredentialsException) {
            logger.error("Senha incorreta para o usuário: $email")
            throw AuthenticationFailedException("Senha incorreta")
        } catch (ex: UserNotFoundException) {
            logger.error("Usuário não encontrado: ${ex.message}")
            throw AuthenticationFailedException("Usuário não encontrado com o email: $email")
        } catch (ex: Exception) {
            logger.error("Erro inesperado durante a autenticação: ${ex.message}")
            throw ex
        }
    }

    private fun generateTokensForUser(user: UserDetails): TokenResponse {
        val token = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        logger.info("Tokens gerados com sucesso para o usuário: ${user.username}")
        return TokenResponse(token, refreshToken)
    }
}