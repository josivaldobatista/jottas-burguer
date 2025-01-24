package com.jfb.jottasburguer.model.dto

data class OrderRequest(
    val customerId: Long, // ID do cliente
    val items: List<OrderItemRequest> // Itens do pedido
)

data class OrderItemRequest(
    val productId: Long, // ID do produto
    val quantity: Int // Quantidade
)