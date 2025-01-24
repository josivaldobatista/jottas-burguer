package com.jfb.jottasburguer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Exceção lançada quando um usuário com email duplicado tenta ser salvo.
 */
@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
class DuplicateUserException(message: String) : RuntimeException(message)