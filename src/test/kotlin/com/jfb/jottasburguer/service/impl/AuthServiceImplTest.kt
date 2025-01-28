package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.AuthenticationFailedException
import com.jfb.jottasburguer.exception.InvalidTokenException
import com.jfb.jottasburguer.exception.UserNotFoundException
import com.jfb.jottasburguer.model.dto.LoginRequest
import com.jfb.jottasburguer.model.dto.TokenResponse
import com.jfb.jottasburguer.repository.UserRepository
import com.jfb.jottasburguer.security.JwtService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

@ExtendWith(MockitoExtension::class)
class AuthServiceImplTest {

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private lateinit var authUserService: UserDetailsService

    @Mock
    private lateinit var jwtService: JwtService

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var authService: AuthServiceImpl

    private val email = "test@example.com"
    private val password = "password123"
    private val token = "access-token"
    private val refreshToken = "refresh-token"

    private lateinit var userDetails: UserDetails

    @BeforeEach
    fun setUp() {
        userDetails = User.withUsername(email)
            .password(password)
            .authorities("ROLE_USER")
            .build()
    }

    @Test
    fun `login should return TokenResponse when credentials are valid`() {
        // Arrange
        val loginRequest = LoginRequest(email, password)
        `when`(userRepository.existsByEmail(email)).thenReturn(true)
        `when`(authUserService.loadUserByUsername(email)).thenReturn(userDetails)
        `when`(jwtService.generateToken(userDetails)).thenReturn(token)
        `when`(jwtService.generateRefreshToken(userDetails)).thenReturn(refreshToken)

        // Act
        val result = authService.login(loginRequest)

        // Assert
        assertEquals(token, result.token)
        assertEquals(refreshToken, result.refreshToken)
        verify(authenticationManager).authenticate(UsernamePasswordAuthenticationToken(email, password))
    }

    @Test
    fun `login should throw UserNotFoundException when email does not exist`() {
        // Arrange
        val loginRequest = LoginRequest(email, password)
        `when`(userRepository.existsByEmail(email)).thenReturn(false)

        // Act & Assert
        assertThrows(AuthenticationFailedException::class.java) {
            authService.login(loginRequest)
        }
    }

    @Test
    fun `login should throw AuthenticationFailedException when password is incorrect`() {
        // Arrange
        val loginRequest = LoginRequest(email, password)
        `when`(userRepository.existsByEmail(email)).thenReturn(true)
        `when`(authenticationManager.authenticate(any()))
            .thenThrow(BadCredentialsException("Bad credentials"))

        // Act & Assert
        assertThrows(AuthenticationFailedException::class.java) {
            authService.login(loginRequest)
        }
    }

    @Test
    fun `refreshToken should return new TokenResponse when refresh token is valid`() {
        // Arrange
        val tokenResponse = TokenResponse(token, refreshToken)
        `when`(jwtService.extractUsername(refreshToken)).thenReturn(email)
        `when`(authUserService.loadUserByUsername(email)).thenReturn(userDetails)
        `when`(jwtService.isTokenValid(refreshToken, userDetails)).thenReturn(true)
        `when`(jwtService.generateToken(userDetails)).thenReturn("new-access-token")
        `when`(jwtService.generateRefreshToken(userDetails)).thenReturn("new-refresh-token")

        // Act
        val result = authService.refreshToken(tokenResponse)

        // Assert
        assertEquals("new-access-token", result.token)
        assertEquals("new-refresh-token", result.refreshToken)
    }

    @Test
    fun `refreshToken should throw InvalidTokenException when refresh token is invalid`() {
        // Arrange
        val tokenResponse = TokenResponse(token, refreshToken)
        `when`(jwtService.extractUsername(refreshToken)).thenReturn(email)
        `when`(authUserService.loadUserByUsername(email)).thenReturn(userDetails)
        `when`(jwtService.isTokenValid(refreshToken, userDetails)).thenReturn(false)

        // Act & Assert
        assertThrows(InvalidTokenException::class.java) {
            authService.refreshToken(tokenResponse)
        }
    }
}