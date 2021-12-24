package com.oriolsoler.security.application.accessverification

interface TokenVerification {
    fun validate(token: String): String
}