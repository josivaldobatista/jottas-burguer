package com.jfb.jottasburguer.exception

import java.time.Instant

open class StandardError(
    open val timestamp: Instant = Instant.now(),
    open val status: Int = 0,
    open val error: String = "",
    open val message: String? = null,
    open val path: String = ""
)