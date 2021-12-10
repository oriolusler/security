package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification

interface VerifyServiceRepository {
    fun save(verification: UserVerification)
    fun getUnusedBy(user: User): UserVerification
}