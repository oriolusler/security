package com.oriolsoler.security.application.login

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserId

interface LoginUserRepository {
    fun getBy(email: String): User
    fun getBy(userId: UserId): User
}