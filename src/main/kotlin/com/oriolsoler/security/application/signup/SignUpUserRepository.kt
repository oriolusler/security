package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserRole

interface SignUpUserRepository {
    fun save(email: String, password: String, name: String, phone: String, roles: List<UserRole>): User
}