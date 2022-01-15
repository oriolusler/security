package com.oriolsoler.security.domain.verification

import com.oriolsoler.security.domain.user.User
import java.time.LocalDateTime

class Verification(
    val verification: String,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val expirationDate: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    val validated: Boolean = false,
    val usable: Boolean = true
) {
    fun checkIfExpired(now: LocalDateTime) {
        if (now.isAfter(expirationDate)) {
            throw VerificationExpiredException()
        }
    }

    fun checkIfAlreadyValidated() {
        if (validated) {
            throw VerificationAlreadyVerifiedException()
        }
    }

    fun checkIfNotValidated() {
        if (!validated) {
            throw VerificationNotVerifiedException()
        }
    }

    fun checkIfUsable() {
        if (!usable) {
            throw VerificationNotUsableException()
        }
    }
}

data class UserVerification(
    val user: User,
    val verification: Verification
)