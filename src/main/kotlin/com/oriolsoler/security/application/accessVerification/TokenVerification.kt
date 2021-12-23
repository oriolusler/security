package com.oriolsoler.security.application.accessVerification

interface TokenVerification {
    fun validate(token: String): String
    fun isValid(token: String): Boolean
}