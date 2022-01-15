package com.oriolsoler.security.application

import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.UserVerification

interface VerifyServiceRepository {
    fun save(userVerification: UserVerification)
    fun getBy(user: User, verification: String): UserVerification
    fun setToUsed(userVerification: UserVerification)
    fun setToDeleted(userVerification: UserVerification)
}