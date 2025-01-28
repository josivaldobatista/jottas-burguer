package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.UserNotFoundException
import com.jfb.jottasburguer.model.entity.AppUser
import com.jfb.jottasburguer.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserDetailsServiceImplTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userDetailsService: UserDetailsServiceImpl

    private lateinit var appUser: AppUser

    @BeforeEach
    fun setUp() {
        appUser = AppUser(
            id = 1L,
            email = "user@example.com",
            hashedPassword = "hashedPassword123",
            roles = setOf("USER")
        )
    }

    @Test
    fun `loadUserByUsername should return UserDetails when user is found`() {
        // Arrange
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(appUser))

        // Act
        val userDetails: UserDetails = userDetailsService.loadUserByUsername("user@example.com")

        // Assert
        assertNotNull(userDetails)
        assertEquals("user@example.com", userDetails.username)
        assertEquals("hashedPassword123", userDetails.password)
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("USER")))
        verify(userRepository, times(1)).findByEmail("user@example.com")
    }

    @Test
    fun `loadUserByUsername should throw IllegalArgumentException when email is empty`() {
        // Arrange
        val emptyEmail = ""

        // Act & Assert
        val exception = assertThrows(IllegalArgumentException::class.java) {
            userDetailsService.loadUserByUsername(emptyEmail)
        }

        assertEquals("Email não pode ser nulo ou vazio", exception.message)
        verify(userRepository, never()).findByEmail(anyString())
    }

    @Test
    fun `loadUserByUsername should throw UserNotFoundException when user is not found`() {
        // Arrange
        `when`(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows(UserNotFoundException::class.java) {
            userDetailsService.loadUserByUsername("nonexistent@example.com")
        }

        assertEquals("Usuário não encontrado com o email: nonexistent@example.com", exception.message)
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com")
    }

    @Test
    fun `loadUserByUsername should return UserDetails with correct authorities`() {
        // Arrange
        val appUserWithRoles = AppUser(
            id = 1L,
            email = "user@example.com",
            hashedPassword = "hashedPassword123",
            roles = setOf("USER", "ADMIN")
        )
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(appUserWithRoles))

        // Act
        val userDetails: UserDetails = userDetailsService.loadUserByUsername("user@example.com")

        // Assert
        assertNotNull(userDetails)
        assertEquals("user@example.com", userDetails.username)
        assertEquals("hashedPassword123", userDetails.password)
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("USER")))
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("ADMIN")))
    }
}