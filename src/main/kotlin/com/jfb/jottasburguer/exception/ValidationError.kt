package com.jfb.jottasburguer.exception

import java.time.Instant

class ValidationError : StandardError {
    private var errors: MutableList<FieldMessage> = ArrayList()

    constructor() : super()

    constructor(
        timestamp: Instant,
        status: Int,
        error: String,
        message: String?,
        path: String
    ) : super(timestamp, status, error, message, path)

    fun addError(fieldName: String, message: String) {
        errors.add(FieldMessage(fieldName, message))
    }
}