package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.DuplicateUserException
import com.jfb.jottasburguer.exception.UserNotFoundException
import com.jfb.jottasburguer.model.dto.UserRequest
import com.jfb.jottasburguer.model.dto.UserResponse
import com.jfb.jottasburguer.model.entity.User
import com.jfb.jottasburguer.repository.UserRepository
import com.jfb.jottasburguer.service.UserService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.slf4j.LoggerFactory

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun createUser(request: UserRequest): UserResponse {
        logger.info("Criando usuário com email: ${request.email}")

        validateUserRequest(request)
        val user = buildUserFromRequest(request)

        try {
            val savedUser = userRepository.save(user)
            logger.info("Usuário criado com sucesso: ${savedUser.email}")
            return mapToUserResponse(savedUser)
        } catch (ex: DataIntegrityViolationException) {
            handleDuplicateEmailException(ex, request.email)
            throw ex // Relança outras exceções de violação de integridade
        }
    }

    override fun findAllUsers(): List<UserResponse> {
        logger.info("Buscando todos os usuários")
        return userRepository.findAll().map { mapToUserResponse(it) }
    }

    override fun findUserById(id: Long): UserResponse {
        logger.info("Buscando usuário pelo ID: $id")
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("Usuário não encontrado com o ID: $id") }
        return mapToUserResponse(user)
    }

    override fun updateUser(id: Long, request: UserRequest): UserResponse {
        logger.info("Atualizando usuário com ID: $id")

        validateUserRequest(request)
        val user = userRepository.findById(id)
            .orElseThrow { UserNotFoundException("Usuário não encontrado com o ID: $id") }

        updateUserFromRequest(user, request)

        try {
            val updatedUser = userRepository.save(user)
            logger.info("Usuário atualizado com sucesso: ${updatedUser.email}")
            return mapToUserResponse(updatedUser)
        } catch (ex: DataIntegrityViolationException) {
            handleDuplicateEmailException(ex, request.email)
            throw ex // Relança outras exceções de violação de integridade
        }
    }

    override fun deleteUser(id: Long) {
        logger.info("Deletando usuário com ID: $id")

        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("Usuário não encontrado com o ID: $id")
        }
        userRepository.deleteById(id)
        logger.info("Usuário deletado com sucesso: $id")
    }

    private fun validateUserRequest(request: UserRequest) {
        if (!StringUtils.hasText(request.email)) {
            throw IllegalArgumentException("Email não pode ser nulo ou vazio")
        }
        if (!StringUtils.hasText(request.password)) {
            throw IllegalArgumentException("Senha não pode ser nula ou vazia")
        }
    }

    private fun buildUserFromRequest(request: UserRequest): User {
        return User(
            email = request.email,
            hashedPassword = passwordEncoder.encode(request.password),
            roles = request.roles
        )
    }

    private fun updateUserFromRequest(user: User, request: UserRequest) {
        user.email = request.email
        user.hashedPassword = passwordEncoder.encode(request.password)
        user.roles = request.roles
    }

    private fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(user.id!!, user.email, user.roles)
    }

    private fun handleDuplicateEmailException(ex: DataIntegrityViolationException, email: String) {
        if (ex.message?.contains("users_email_key") == true) {
            logger.error("Email duplicado: $email")
            throw DuplicateUserException("Já existe um usuário com o email $email")
        }
    }
}