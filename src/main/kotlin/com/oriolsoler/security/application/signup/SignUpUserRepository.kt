package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User

interface SignUpUserRepository {
    fun save(user: User): User
}