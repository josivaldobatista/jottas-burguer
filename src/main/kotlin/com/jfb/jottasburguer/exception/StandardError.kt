package com.jfb.jottasburguer.exception

import java.io.Serializable
import java.time.Instant

open class StandardError( // Adicione "open" aqui
    var timestamp: Instant = Instant.now(),
    var status: Int = 0,
    var error: String = "",
    var message: String? = "",
    var path: String = ""
) : Serializable