package com.jfb.jottasburguer.exception

import com.jfb.jottasburguer.controller.AuthController
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class ResourceExceptionHandler {

    private val logger = LoggerFactory.getLogger(ResourceExceptionHandler::class.java)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        e: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<StandardError> {
        val status = HttpStatus.NOT_FOUND
        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Recurso não encontrado",
            message = e.message ?: "O recurso solicitado não foi encontrado.",
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(DuplicateUserException::class)
    fun handleDuplicateUserException(
        e: DuplicateUserException,
        request: HttpServletRequest
    ): ResponseEntity<StandardError> {
        val status = HttpStatus.CONFLICT
        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Conflito de dados",
            message = e.message ?: "O email fornecido já está em uso.",
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(DatabaseException::class)
    fun handleDatabaseException(
        e: DatabaseException,
        request: HttpServletRequest
    ): ResponseEntity<StandardError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Erro no banco de dados",
            message = e.message ?: "Ocorreu um erro ao acessar o banco de dados.",
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationError> {
        val status = HttpStatus.UNPROCESSABLE_ENTITY
        val err = ValidationError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Erro de validação",
            message = "Um ou mais campos estão inválidos. Corrija e tente novamente.",
            path = request.requestURI
        )

        for (f in e.bindingResult.fieldErrors) {
            err.addError(f.field, f.defaultMessage ?: "Erro desconhecido no campo ${f.field}.")
        }

        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(AuthenticationFailedException::class)
    fun handleAuthenticationFailedException(
        e: AuthenticationFailedException,
        request: HttpServletRequest
    ): ResponseEntity<StandardError> {
        val status = HttpStatus.UNAUTHORIZED
        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Falha na autenticação",
            message = e.message ?: "Credenciais inválidas. Verifique seu email e senha.",
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        e: Exception,
        request: HttpServletRequest
    ): ResponseEntity<StandardError> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR

        // Log da exceção completa
        logger.error("Erro inesperado: ${e.message}", e)

        val err = StandardError(
            timestamp = Instant.now(),
            status = status.value(),
            error = "Erro interno no servidor",
            message = "Ocorreu um erro inesperado. Tente novamente mais tarde.",
            path = request.requestURI
        )
        return ResponseEntity.status(status).body(err)
    }
}