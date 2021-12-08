package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER

class User(
    val id: UserId = UserId(),
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val roles: List<UserRole> = listOf(ROLE_USER)
)