package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.Verification

interface VerifyServiceRepository {
    fun save(verification: Verification)
    fun getUnusedBy(user: User): Verification
}