package com.jfb.jottasburguer.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import org.slf4j.LoggerFactory
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

    fun generateToken(userDetails: UserDetails, additionalClaims: Map<String, Any> = emptyMap()): String {
        Assert.notNull(userDetails, "UserDetails não pode ser nulo")
        logger.info("Gerando token para o usuário: ${userDetails.username}")

        val builder = Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key, SIGNATURE_ALGORITHM)

        additionalClaims.forEach { (key, value) ->
            builder.claim(key, value)
        }

        val token = builder.compact()
        logger.info("Token gerado: $token")
        return token
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        logger.info("Gerando refresh token para o usuário: ${userDetails.username}")
        return generateToken(userDetails, mapOf("type" to "refresh"))
    }

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

    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = extractClaims(token).expiration
        val isExpired = expirationDate.before(Date())
        logger.info("Token expirado: $isExpired")
        return isExpired
    }
}