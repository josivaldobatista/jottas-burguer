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
    var ingredients: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = true)
    var imageUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_type_id", nullable = false)
    var foodType: FoodType // Relacionamento com FoodType
)