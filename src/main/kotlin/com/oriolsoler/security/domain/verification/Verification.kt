package com.oriolsoler.security.domain.verification

import com.oriolsoler.security.domain.user.User
import java.time.LocalDateTime

class Verification(
    val verification: String,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val expirationDate: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    val used: Boolean = false,
    val deleted: Boolean = false
) {
    fun validateIfExpired(now: LocalDateTime) {
        if (now.isAfter(expirationDate)) {
            throw VerificationExpiredException()
        }
    }

    fun validateIfUsed() {
        if (used) {
            throw VerificationUsedException()
        }
    }

    fun validateIfNotUsed() {
        if (!used) {
            throw VerificationNotVerifiedException()
        }
    }

    fun validateIfDeleted() {
        if (deleted) {
            throw VerificationDeletedException()
        }
    }
}

data class UserVerification(
    val user: User,
    val verification: Verification
)