package com.jfb.jottasburguer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exceção lançada quando um token inválido é fornecido.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED) // Retorna status HTTP 401 (Unauthorized)
class InvalidTokenException(message: String) : RuntimeException(message)