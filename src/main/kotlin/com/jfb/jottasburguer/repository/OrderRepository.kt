package com.jfb.jottasburguer.repository

import com.jfb.jottasburguer.model.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>