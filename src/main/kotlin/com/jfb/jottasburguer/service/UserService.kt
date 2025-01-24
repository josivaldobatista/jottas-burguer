package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.model.dto.UserRequest
import com.jfb.jottasburguer.model.dto.UserResponse
import jakarta.validation.Valid

interface UserService {
    fun createUser(@Valid request: UserRequest): UserResponse
    fun findAllUsers(): List<UserResponse>
    fun findUserById(id: Long): UserResponse
    fun updateUser(id: Long, request: UserRequest): UserResponse
    fun deleteUser(id: Long)
}