package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification

interface VerifyServiceRepository {
    fun save(userVerification: UserVerification)
    fun getUnusedBy(user: User): UserVerification
    fun getUnusedBy(user: User, verification: String): UserVerification
    fun setToUsed(userVerification: UserVerification)
}