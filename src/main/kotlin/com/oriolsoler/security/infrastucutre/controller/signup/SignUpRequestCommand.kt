package com.oriolsoler.security.infrastucutre.controller.signup

import com.oriolsoler.security.domain.user.UserRole

class SignUpRequestCommand(
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
    val roles: List<UserRole>
)