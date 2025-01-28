package com.jfb.jottasburguer.repository

import com.jfb.jottasburguer.model.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, Long> {

    fun findByOrderId(orderId: Long): List<OrderItem>
    fun findByProductId(productId: Long): List<OrderItem>
    fun findByOrderIdAndProductId(orderId: Long, productId: Long): List<OrderItem>

    @Query("SELECT oi FROM OrderItem oi LEFT JOIN FETCH oi.product WHERE oi.order.id = :orderId")
    fun findByOrderIdTest(orderId: Long): List<OrderItem>
}