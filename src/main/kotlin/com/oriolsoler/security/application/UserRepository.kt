package com.oriolsoler.security.application

import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.user.UserId

interface UserRepository {
    fun getBy(email: String): User
    fun getBy(userId: UserId): User
    fun save(user: User): User
    fun setUnlocked(user: User)
    fun checkIfUserAlreadyExists(email: String)
}