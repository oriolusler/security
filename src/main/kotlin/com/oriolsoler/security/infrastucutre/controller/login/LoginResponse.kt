package com.oriolsoler.security.infrastucutre.controller.login

import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.user.UserId

class LoginResponse(
    val token: Token,
    val user: ResponseUser
)

data class ResponseUser(val id: String, val email: String)