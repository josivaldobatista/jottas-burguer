package com.jfb.jottasburguer.model.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val roles: Set<String>
)