package com.jfb.jottasburguer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exceção lançada quando um usuário não é encontrado.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Not Found
class UserNotFoundException(message: String) : RuntimeException(message)