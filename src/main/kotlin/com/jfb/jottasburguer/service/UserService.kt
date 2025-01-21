package com.jfb.jottasburguer.service

import com.jfb.jottasburguer.model.dto.UserRequest
import com.jfb.jottasburguer.model.dto.UserResponse

interface UserService {
    fun createUser(request: UserRequest): UserResponse
    fun findAllUsers(): List<UserResponse>
    fun findUserById(id: Long): UserResponse
    fun updateUser(id: Long, request: UserRequest): UserResponse
    fun deleteUser(id: Long)
}