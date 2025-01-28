package com.jfb.jottasburguer.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: Customer? = null,

    @Column(nullable = false)
    var total: Double = 0.0,

    @Column(nullable = false)
    var status: String = OrderStatus.RECEIVED.name,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(customer = null, total = 0.0)
}

enum class OrderStatus {
    RECEIVED, PROCESSING, COMPLETED, CANCELLED
}