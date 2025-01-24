package com.jfb.jottasburguer.controller

import com.jfb.jottasburguer.model.dto.OrderRequest
import com.jfb.jottasburguer.model.dto.OrderResponse
import com.jfb.jottasburguer.service.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    fun createOrder(@Valid @RequestBody request: OrderRequest): ResponseEntity<OrderResponse> {
        val createdOrder = orderService.createOrder(request)
        return ResponseEntity(createdOrder, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun findOrderById(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val order = orderService.findOrderById(id)
        return ResponseEntity(order, HttpStatus.OK)
    }

    @PutMapping("/{id}/status")
    fun updateOrderStatus(@PathVariable id: Long, @RequestParam status: String): ResponseEntity<OrderResponse> {
        val updatedOrder = orderService.updateOrderStatus(id, status)
        return ResponseEntity(updatedOrder, HttpStatus.OK)
    }
}