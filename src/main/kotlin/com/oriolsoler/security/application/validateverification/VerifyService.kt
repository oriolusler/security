package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.domain.verification.Verification

interface VerifyService {
    fun generate(): Verification
    fun validateIfValid(verification: Verification)
    fun validateIfNotUsed(verification: Verification)
}