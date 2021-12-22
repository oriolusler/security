package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER

class User(
    val id: UserId = UserId(),
    val email: String = "",
    val password: String = "",
    val roles: List<UserRole> = listOf(ROLE_USER),
    val locked: Boolean = true
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}