package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User

interface SignUpUserRepository {
    fun save(email: String, password: String): User
}