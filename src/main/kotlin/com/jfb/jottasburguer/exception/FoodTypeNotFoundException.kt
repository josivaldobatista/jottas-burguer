package com.jfb.jottasburguer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class FoodTypeNotFoundException(message: String) : RuntimeException(message)