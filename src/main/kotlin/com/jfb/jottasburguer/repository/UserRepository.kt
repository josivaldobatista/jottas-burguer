package com.jfb.jottasburguer.repository

import com.jfb.jottasburguer.model.entity.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<AppUser, Long> {
    fun findByEmail(email: String): Optional<AppUser>
    fun existsByEmail(email: String): Boolean
}