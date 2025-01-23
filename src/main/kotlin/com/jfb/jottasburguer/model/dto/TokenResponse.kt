package com.jfb.jottasburguer.model.dto

data class TokenResponse(
    val token: String,          // Token de acesso (JWT)
    val refreshToken: String    // Refresh token (JWT)
)