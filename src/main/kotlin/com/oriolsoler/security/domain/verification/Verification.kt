package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.verification.VerificationExpiredException
import com.oriolsoler.security.domain.verification.VerificationUsedException
import java.time.LocalDateTime

class Verification(
    val verification: String,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val expirationDate: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    val used: Boolean = false
) {
    fun isValid(now: LocalDateTime): Boolean {
        if (used) {
            throw VerificationUsedException()
        }
        if (now.isAfter(expirationDate)) {
            throw VerificationExpiredException()
        }
        return true
    }
}

data class UserVerification(
    val user: User,
    val verification: Verification
)