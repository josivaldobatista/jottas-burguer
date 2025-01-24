package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.model.dto.LoginRequest
import com.jfb.jottasburguer.model.dto.TokenResponse
import jakarta.validation.Valid

interface AuthService {

    fun login(@Valid request: LoginRequest): TokenResponse
    fun refreshToken(@Valid request: TokenResponse): TokenResponse
}