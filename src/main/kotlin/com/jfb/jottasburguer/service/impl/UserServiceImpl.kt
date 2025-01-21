package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.UserNotFoundException
import com.jfb.jottasburguer.model.dto.UserRequest
import com.jfb.jottasburguer.model.dto.UserResponse
import com.jfb.jottasburguer.model.entity.User
import com.jfb.jottasburguer.repository.UserRepository
import com.jfb.jottasburguer.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun createUser(request: UserRequest): UserResponse {
        val user = User(
            email = request.email,
            hashedPassword = passwordEncoder.encode(request.password),
            roles = request.roles
        )
        val savedUser = userRepository.save(user)
        return UserResponse(savedUser.id!!, savedUser.email, savedUser.roles)
    }

    override fun findAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { UserResponse(it.id!!, it.email, it.roles) }
    }

    override fun findUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }
        return UserResponse(user.id!!, user.email, user.roles)
    }

    override fun updateUser(id: Long, request: UserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with id: $id") }
        user.email = request.email
        user.hashedPassword = passwordEncoder.encode(request.password)
        user.roles = request.roles
        val updatedUser = userRepository.save(user)
        return UserResponse(updatedUser.id!!, updatedUser.email, updatedUser.roles)
    }

    override fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User not found with id: $id")
        }
        userRepository.deleteById(id)
    }
}