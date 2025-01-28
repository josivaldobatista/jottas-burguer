package com.jfb.jottasburguer.repository

import com.jfb.jottasburguer.model.dto.OrderItemResponse
import com.jfb.jottasburguer.model.dto.OrderResponse
import com.jfb.jottasburguer.model.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderRepository : JpaRepository<Order, Long> {

    @Query(
        """
        SELECT new com.jfb.jottasburguer.model.dto.OrderResponse(
            o.id,
            o.customer.id,
            o.total,
            o.status,
            o.createdAt,
            o.updatedAt
        )
        FROM Order o
        WHERE o.id = :id
    """
    )
    fun findOrderById(@Param("id") id: Long): OrderResponse?

    @Query(
        """
        SELECT new com.jfb.jottasburguer.model.dto.OrderItemResponse(
            i.product.id,
            i.product.name,
            i.quantity,
            i.price
        )
        FROM OrderItem i
        WHERE i.order.id = :orderId
    """
    )
    fun findOrderItemsByOrderId(@Param("orderId") orderId: Long): List<OrderItemResponse>
}