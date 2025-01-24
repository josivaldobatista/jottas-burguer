package com.jfb.jottasburguer.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ProductRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Ingredients are required")
    val ingredients: String,

    @field:NotBlank(message = "Description is required")
    val description: String,

    @field:NotNull(message = "Price is required")
    @field:Positive(message = "Price must be positive")
    val price: Double,

    val imageUrl: String? = null,

    @field:NotNull(message = "Food type ID is required")
    val foodTypeId: Long // Agora é um ID que referencia FoodType
)