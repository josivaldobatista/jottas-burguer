package com.jfb.jottasburguer.model.dto

import java.time.LocalDateTime

data class OrderResponse(
    val id: Long,
    val customerId: Long,
    val total: Double,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val items: List<OrderItemResponse>
)

data class OrderItemResponse(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
)