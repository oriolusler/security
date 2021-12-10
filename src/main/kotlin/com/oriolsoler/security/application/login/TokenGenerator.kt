package com.oriolsoler.security.application.login

import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.user.UserId

interface TokenGenerator {
    fun generate(userId: UserId): Token
    fun getUserIdFromToken(token: String): String
    fun isValid(token: String): Boolean
}