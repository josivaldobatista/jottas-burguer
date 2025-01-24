package com.jfb.jottasburguer.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.util.*
import javax.crypto.SecretKey

@Service
final class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {
    private val logger = LoggerFactory.getLogger(JwtService::class.java)
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    companion object {
        private val SIGNATURE_ALGORITHM = Jwts.SIG.HS256
    }

    /**
     * Gera um token JWT para o usuário, incluindo suas roles (autoridades) como uma claim.
     */
    fun generateToken(userDetails: UserDetails, additionalClaims: Map<String, Any> = emptyMap()): String {
        Assert.notNull(userDetails, "UserDetails não pode ser nulo")
        logger.info("Gerando token para o usuário: ${userDetails.username}")

        // Extrai as roles (autoridades) do UserDetails
        val roles = userDetails.authorities.map { it.authority }

        // Cria as claims do token, incluindo as roles
        val claims = mutableMapOf<String, Any>(
            "roles" to roles // Adiciona as roles como uma claim
        )
        claims.putAll(additionalClaims) // Adiciona claims adicionais, se houver

        // Constrói o token JWT
        val token = Jwts.builder()
            .subject(userDetails.username) // Define o subject (normalmente o email ou username)
            .issuedAt(Date()) // Define a data de emissão
            .expiration(Date(System.currentTimeMillis() + expiration)) // Define a data de expiração
            .claims(claims) // Adiciona as claims (incluindo as roles)
            .signWith(key, SIGNATURE_ALGORITHM) // Assina o token
            .compact()

        logger.info("Token gerado: $token")
        return token
    }

    /**
     * Gera um refresh token para o usuário.
     */
    fun generateRefreshToken(userDetails: UserDetails): String {
        logger.info("Gerando refresh token para o usuário: ${userDetails.username}")
        return generateToken(userDetails, mapOf("type" to "refresh")) // Adiciona uma claim "type" para identificar o refresh token
    }

    /**
     * Extrai o username (subject) do token JWT.
     */
    fun extractUsername(token: String): String {
        logger.info("Extraindo username do token: $token")
        return try {
            val username = extractClaims(token).subject
            logger.info("Username extraído: $username")
            username
        } catch (e: JwtException) {
            logger.error("Erro ao extrair username do token: ${e.message}")
            throw JwtException("Token inválido ou expirado", e)
        }
    }

    /**
     * Verifica se o token JWT é válido para o UserDetails fornecido.
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        if (token.isBlank()) {
            logger.error("Token não pode ser nulo ou vazio")
            return false
        }

        logger.info("Validando token para o usuário: ${userDetails.username}")
        return try {
            val username = extractUsername(token)
            val isValid = username == userDetails.username && !isTokenExpired(token)
            logger.info("Token válido: $isValid")
            isValid
        } catch (e: JwtException) {
            logger.error("Erro ao validar token: ${e.message}")
            false
        }
    }

    /**
     * Extrai as claims (dados) do token JWT.
     */
    private fun extractClaims(token: String): Claims {
        if (token.isBlank()) {
            logger.error("Token não pode ser nulo ou vazio")
            throw JwtException("Token não pode ser nulo ou vazio")
        }

        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * Verifica se o token JWT está expirado.
     */
    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = extractClaims(token).expiration
        val isExpired = expirationDate.before(Date())
        logger.info("Token expirado: $isExpired")
        return isExpired
    }

    /**
     * Extrai as roles (autoridades) do token JWT.
     */
    fun extractRoles(token: String): List<String> {
        logger.info("Extraindo roles do token: $token")
        return try {
            val claims = extractClaims(token)
            @Suppress("UNCHECKED_CAST")
            val roles = claims["roles"] as? List<String> ?: emptyList()
            logger.info("Roles extraídas: $roles")
            roles
        } catch (e: JwtException) {
            logger.error("Erro ao extrair roles do token: ${e.message}")
            emptyList()
        }
    }
}