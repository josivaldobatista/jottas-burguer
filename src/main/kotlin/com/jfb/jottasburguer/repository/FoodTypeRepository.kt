package com.jfb.jottasburguer.repository

import com.jfb.jottasburguer.model.entity.FoodType
import org.springframework.data.jpa.repository.JpaRepository

interface FoodTypeRepository : JpaRepository<FoodType, Long> {
    fun findByName(name: String): FoodType?
}