package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.domain.verification.Verification

interface VerifyService {
    fun generate(): Verification
    fun validateIfExpired(verification: Verification)
    fun validateIfNotUsed(verification: Verification)
    fun validateIfNotDeleted(verification: Verification)
    fun validateIfUsed(verification: Verification)
}