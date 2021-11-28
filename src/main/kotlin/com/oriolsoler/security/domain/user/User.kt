package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole

class User(
    val id: UserId?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val password: String?,
    val roles: List<UserRole>?
) {
    constructor() : this(
        UserId(),
        "",
        "",
        "",
        "",
        ArrayList()
    )

    constructor(name: String, email: String, phone: String, password: String) : this(
        UserId(),
        name,
        email,
        phone,
        password,
        ArrayList()
    )

    constructor(email: String) : this(
        UserId(),
        "",
        email,
        "",
        "",
        ArrayList()
    )
}