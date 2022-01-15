package com.oriolsoler.security.application

import com.oriolsoler.security.domain.verification.Verification

interface VerifyService {
    fun generate(): Verification
    fun checkIfExpired(verification: Verification)
    fun checkIfNotValidated(verification: Verification)
    fun checkIfUsable(verification: Verification)
    fun checkIfAlreadyValidated(verification: Verification)
}