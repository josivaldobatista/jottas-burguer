package com.jfb.jottasburguer.model.dto

data class CustomerResponse(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val address: String
)