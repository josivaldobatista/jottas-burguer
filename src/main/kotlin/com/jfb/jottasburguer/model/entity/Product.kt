package com.jfb.jottasburguer.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "products")
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var ingredients: String, // Novo campo

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = true) // Pode ser nulo
    var imageUrl: String? = null,

    @Column(name = "food_type_id", nullable = false) // Novo campo
    var foodTypeId: Long
)