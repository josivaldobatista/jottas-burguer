package com.jfb.jottasburguer.exception

import java.time.Instant

class ValidationError : StandardError {
    // Lista de erros de campo
    private val errors: MutableList<FieldMessage> = ArrayList()

    // Construtor padrão
    constructor() : super()

    // Construtor com parâmetros
    constructor(
        timestamp: Instant,
        status: Int,
        error: String,
        message: String?,
        path: String
    ) : super(timestamp, status, error, message, path)

    // Método para adicionar erros de campo
    fun addError(fieldName: String, message: String) {
        errors.add(FieldMessage(fieldName, message))
    }

    // Método para obter a lista de erros (pode ser útil para serialização)
    fun getErrors(): List<FieldMessage> {
        return errors
    }

    // Classe interna para representar erros de campo
    data class FieldMessage(
        var fieldName: String = "",
        var message: String = ""
    )
}