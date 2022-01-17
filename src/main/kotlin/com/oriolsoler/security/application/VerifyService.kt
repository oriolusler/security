package com.oriolsoler.security.application

import com.oriolsoler.security.domain.verification.Verification
import com.oriolsoler.security.domain.verification.VerificationType

interface VerifyService {
    fun generate(type: VerificationType): Verification
    fun checkIfExpired(verification: Verification)
    fun checkIfNotValidated(verification: Verification)
    fun checkIfUsable(verification: Verification)
    fun checkIfAlreadyValidated(verification: Verification)
}