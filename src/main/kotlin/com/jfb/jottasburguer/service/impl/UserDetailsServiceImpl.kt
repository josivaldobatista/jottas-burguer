package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.UserNotFoundException
import com.jfb.jottasburguer.model.entity.AppUser
import com.jfb.jottasburguer.repository.UserRepository
import com.jfb.jottasburguer.service.UserDetailsService
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    private val logger = LoggerFactory.getLogger(UserDetailsServiceImpl::class.java)

    override fun loadUserByUsername(email: String): UserDetails {
        logger.info("Tentando carregar usuário pelo email: $email")

        // Validação de entrada
        if (!StringUtils.hasText(email)) {
            logger.error("Email não pode ser nulo ou vazio")
            throw IllegalArgumentException("Email não pode ser nulo ou vazio")
        }

        // Busca o usuário no banco de dados
        val user = userRepository.findByEmail(email)
            .orElseThrow {
                logger.error("Usuário não encontrado com o email: $email")
                UserNotFoundException("Usuário não encontrado com o email: $email")
            }

        logger.info("Usuário encontrado: ${user.email}, roles: ${user.roles}")
        return buildUserDetails(user)
    }

    private fun buildUserDetails(appUser: AppUser): UserDetails {
        // Converte as roles do usuário para GrantedAuthority
        val authorities: List<GrantedAuthority> = appUser.roles.map { role ->
            SimpleGrantedAuthority(role)
        }

        // Constrói o UserDetails
        val userDetails = User
            .withUsername(appUser.email)
            .password(appUser.hashedPassword)
            .authorities(authorities)
            .build()

        logger.info("USER DETAILS construído: $userDetails")
        return userDetails
    }
}