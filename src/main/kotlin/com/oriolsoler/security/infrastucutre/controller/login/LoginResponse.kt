package com.oriolsoler.security.infrastucutre.controller.login

import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole

class LoginResponse(
    val token: String,
    val type: String? = "Bearer",
    val id: UserId,
    val name: String,
    val email: String,
    val roles: List<UserRole>?
) {
    constructor() : this("", "Bearer", UserId(), "", "", emptyList())
}