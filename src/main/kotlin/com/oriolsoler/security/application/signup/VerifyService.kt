package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification

interface VerifyService {
    fun generate(): Verification
    fun isValid(userVerification: UserVerification): Boolean
}