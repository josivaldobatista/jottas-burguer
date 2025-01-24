package com.jfb.jottasburguer.repository

import com.jfb.jottasburguer.model.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByEmail(email: String): Customer?
}