package com.oriolsoler.security.infrastucutre.controller.signup

import com.oriolsoler.security.domain.user.User

class SignUpResponse(private val user: User) {
    fun response(): String {
        return "User with email ${user.email} created successfully"
    }
}

