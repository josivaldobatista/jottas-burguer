package com.jfb.jottasburguer.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "food_types")
data class FoodType(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = true)
    var description: String? = null,

    @Column(nullable = true)
    var imageUrl: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)