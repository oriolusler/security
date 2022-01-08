package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.Verification

interface VerifyService {
    fun generate(): Verification
    fun isValid(userVerification: UserVerification): Boolean
}