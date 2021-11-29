package com.oriolsoler.security.application.login

import com.oriolsoler.security.domain.User

interface LoginUserRepository {
    fun getBy(email: String): User
}