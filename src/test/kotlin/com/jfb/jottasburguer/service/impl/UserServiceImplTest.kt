package com.jfb.jottasburguer.service.impl

import com.jfb.jottasburguer.exception.DuplicateUserException
import com.jfb.jottasburguer.exception.UserNotFoundException
import com.jfb.jottasburguer.model.dto.UserRequest
import com.jfb.jottasburguer.model.dto.UserResponse
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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceImplTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var userService: UserServiceImpl

    private lateinit var userRequest: UserRequest
    private lateinit var appUser: AppUser
    private lateinit var userResponse: UserResponse

    @BeforeEach
    fun setUp() {
        userRequest = UserRequest("test@example.com", "password123", setOf("USER"))
        appUser = AppUser(id = 1L, email = "test@example.com", hashedPassword = "hashedPassword", roles = setOf("USER"))
        userResponse = UserResponse(1L, "test@example.com", setOf("USER"))
    }

    @Test
    fun `createUser should return UserResponse when user is successfully created`() {
        `when`(passwordEncoder.encode(anyString())).thenReturn("hashedPassword")
        `when`(userRepository.save(any(AppUser::class.java))).thenReturn(appUser)

        val result = userService.createUser(userRequest)

        assertNotNull(result)
        assertEquals(userResponse, result)
        verify(userRepository, times(1)).save(any(AppUser::class.java))
    }

    @Test
    fun `createUser should throw DuplicateUserException when email is duplicated`() {
        `when`(passwordEncoder.encode(anyString())).thenReturn("hashedPassword")
        `when`(userRepository.save(any(AppUser::class.java))).thenThrow(DataIntegrityViolationException("users_email_key"))

        assertThrows(DuplicateUserException::class.java) {
            userService.createUser(userRequest)
        }

        verify(userRepository, times(1)).save(any(AppUser::class.java))
    }

    @Test
    fun `findAllUsers should return list of UserResponse`() {
        `when`(userRepository.findAll()).thenReturn(listOf(appUser))

        val result = userService.findAllUsers()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(userResponse, result[0])
        verify(userRepository, times(1)).findAll()
    }

    @Test
    fun `findUserById should return UserResponse when user is found`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(appUser))

        val result = userService.findUserById(1L)

        assertNotNull(result)
        assertEquals(userResponse, result)
        verify(userRepository, times(1)).findById(1L)
    }

    @Test
    fun `findUserById should throw UserNotFoundException when user is not found`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows(UserNotFoundException::class.java) {
            userService.findUserById(1L)
        }

        verify(userRepository, times(1)).findById(1L)
    }

    @Test
    fun `updateUser should return UserResponse when user is successfully updated`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(appUser))
        `when`(passwordEncoder.encode(anyString())).thenReturn("hashedPassword")
        `when`(userRepository.save(any(AppUser::class.java))).thenReturn(appUser)

        val result = userService.updateUser(1L, userRequest)

        assertNotNull(result)
        assertEquals(userResponse, result)
        verify(userRepository, times(1)).findById(1L)
        verify(userRepository, times(1)).save(any(AppUser::class.java))
    }

    @Test
    fun `updateUser should throw UserNotFoundException when user is not found`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows(UserNotFoundException::class.java) {
            userService.updateUser(1L, userRequest)
        }

        verify(userRepository, times(1)).findById(1L)
    }

    @Test
    fun `deleteUser should delete user when user exists`() {
        `when`(userRepository.existsById(1L)).thenReturn(true)

        userService.deleteUser(1L)

        verify(userRepository, times(1)).existsById(1L)
        verify(userRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `deleteUser should throw UserNotFoundException when user does not exist`() {
        `when`(userRepository.existsById(1L)).thenReturn(false)

        assertThrows(UserNotFoundException::class.java) {
            userService.deleteUser(1L)
        }

        verify(userRepository, times(1)).existsById(1L)
        verify(userRepository, times(0)).deleteById(1L)
    }
}