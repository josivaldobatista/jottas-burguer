package com.jfb.jottasburguer.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CustomerRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Phone is required")
    @field:Pattern(
        regexp = "^\\(\\d{2}\\) \\d{5}-\\d{4}$",
        message = "Phone must be in the format (XX) XXXXX-XXXX"
    )
    val phone: String,

    @field:NotBlank(message = "Address is required")
    val address: String
)