package com.jfb.jottasburguer.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun entityNotFound(e: ResourceNotFoundException, request: HttpServletRequest): ResponseEntity<StandardError> {
        val status = HttpStatus.NOT_FOUND
        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Recurso não encontrado",
            message = e.message,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(DatabaseException::class)
    fun databaseException(e: DatabaseException, request: HttpServletRequest): ResponseEntity<StandardError> {
        val status = HttpStatus.BAD_REQUEST
        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Database exception",
            message = e.message,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validationException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ValidationError> {
        val status = HttpStatus.UNPROCESSABLE_ENTITY
        val err = ValidationError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Validation exception",
            message = "Erro de validação nos campos",
            path = request.requestURI
        )

        for (f in e.bindingResult.fieldErrors) {
            err.addError(f.field, f.defaultMessage ?: "Erro desconhecido")
        }

        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(AuthenticationFailedException::class)
    fun handleAuthenticationFailedException(
        ex: AuthenticationFailedException,
        request: HttpServletRequest
    ): ResponseEntity<StandardError> {
        val status = HttpStatus.UNAUTHORIZED
        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Falha na autenticação",
            message = ex.message,
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }
}