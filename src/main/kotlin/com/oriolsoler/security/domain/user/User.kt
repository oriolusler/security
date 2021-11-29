package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER

class User(
    val id: UserId?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val password: String?,
    val roles: List<UserRole>? = listOf(ROLE_USER)
) {
    constructor() : this(
        UserId(),
        "",
        "",
        "",
        "",
        listOf(ROLE_USER)
    )

    constructor(name: String, email: String, phone: String, password: String) : this(
        UserId(),
        name,
        email,
        phone,
        password,
        listOf(ROLE_USER)
    )

    constructor(name: String, email: String, phone: String, password: String, roles: List<UserRole>) : this(
        UserId(),
        name,
        email,
        phone,
        password,
        roles
    )

    constructor(email: String) : this(
        UserId(),
        "",
        email,
        "",
        "",
        listOf(ROLE_USER)
    )
}