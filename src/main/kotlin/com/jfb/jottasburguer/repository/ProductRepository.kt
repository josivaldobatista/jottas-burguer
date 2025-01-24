package com.jfb.jottasburguer.repository

import com.jfb.jottasburguer.model.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long>