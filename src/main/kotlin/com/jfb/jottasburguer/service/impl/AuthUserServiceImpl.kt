package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.repository.UserRepository
import com.jfb.jottasburguer.service.AuthUserService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class AuthUserServiceImpl(
    private val userRepository: UserRepository
) : AuthUserService {

    private val logger = LoggerFactory.getLogger(AuthUserServiceImpl::class.java)

    override fun loadUserByUsername(email: String): UserDetails {
        logger.info("Tentando carregar usuário pelo email: $email")
        val user = userRepository.findByEmail(email)
            .orElseThrow {
                logger.error("Usuário não encontrado com o email: $email")
                UsernameNotFoundException("Usuário não encontrado com o email: $email")
            }
        logger.info("Usuário encontrado: ${user.email}, roles: ${user.roles}")
        logger.info("Senha do Usuário: ${user.hashedPassword}")
        return User
            .withUsername(user.email)
            .password(user.hashedPassword)
            .authorities(user.authorities)
            .build()
    }
}