package com.jfb.jottasburguer.model.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class OrderRequest(
    @field:NotNull(message = "Customer ID is required")
    val customerId: Long,

    @field:NotNull(message = "Items are required")
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: Long,

    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)