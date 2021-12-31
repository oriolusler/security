package com.oriolsoler.security.application.login

import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.user.UserId

interface TokenGenerator {
    fun generate(userId: UserId, expirationDays: Long): Token
}