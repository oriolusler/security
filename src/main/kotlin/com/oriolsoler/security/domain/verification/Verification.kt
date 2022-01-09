package com.oriolsoler.security.domain.verification

import com.oriolsoler.security.domain.user.User
import java.time.LocalDateTime

class Verification(
    val verification: String,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val expirationDate: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    val used: Boolean = false
) {
    fun validateIfValid(now: LocalDateTime) {
        validateIfUsed()
        validateIfExpired(now)
    }

    private fun validateIfExpired(now: LocalDateTime) {
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
        if(!used){
            throw VerificationNotVerifiedException()
        }
    }
}

data class UserVerification(
    val user: User,
    val verification: Verification
)