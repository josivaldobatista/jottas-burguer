package com.jfb.jottasburguer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exceção lançada quando ocorre um erro relacionado ao banco de dados.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
class DatabaseException(message: String?) : RuntimeException(message)