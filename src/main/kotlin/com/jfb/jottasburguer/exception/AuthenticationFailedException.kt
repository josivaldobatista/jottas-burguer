package com.jfb.jottasburguer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exceção lançada quando a autenticação falha.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
class AuthenticationFailedException(message: String) : RuntimeException(message)