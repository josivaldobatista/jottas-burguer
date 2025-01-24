package com.jfb.jottasburguer.model.dto

data class ProductResponse(
    val id: Long,
    val name: String,
    val ingredients: String, // Novo campo
    val description: String,
    val price: Double,
    val imageUrl: String?, // Pode ser nulo
    val foodTypeId: Long // Novo campo
)