package com.oriolsoler.security.application.accessverification

interface TokenVerification {
    fun validateAccessToken(token: String): String
    fun validateRefreshToken(token: String): String
}