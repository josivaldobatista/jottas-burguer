package com.jfb.jottasburguer.exception

import java.io.Serializable

data class FieldMessage(
    var fieldName: String = "",
    var message: String = ""
) : Serializable