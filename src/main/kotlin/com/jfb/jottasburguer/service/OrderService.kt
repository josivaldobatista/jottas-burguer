package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.model.dto.OrderRequest
import com.jfb.jottasburguer.model.dto.OrderResponse

interface OrderService {
    fun createOrder(request: OrderRequest): OrderResponse
    fun findOrderById(id: Long): OrderResponse
    fun updateOrderStatus(id: Long, status: String): OrderResponse
}