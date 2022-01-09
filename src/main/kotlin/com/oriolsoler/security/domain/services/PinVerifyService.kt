package com.oriolsoler.security.domain.services

import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.domain.verification.Verification
import java.util.concurrent.ThreadLocalRandom

class PinVerifyService(private val clock: ClockService, private val minutesValid: Long) : VerifyService {
    override fun generate(): Verification {
        val verification = generateVerificationCode()
        val now = clock.now()
        return Verification(
            verification = verification,
            creationDate = now,
            expirationDate = now.plusMinutes(minutesValid)
        )
    }

    override fun validateIfExpired(verification: Verification) {
        verification.validateIfExpired(clock.now())
    }

    override fun validateIfUsed(verification: Verification) {
        verification.validateIfUsed()
    }

    override fun validateIfNotUsed(verification: Verification) {
        verification.validateIfNotUsed()
    }

    override fun validateIfNotDeleted(verification: Verification) {
        verification.validateIfDeleted()
    }

    private fun generateVerificationCode(): String {
        val randomNumber = ThreadLocalRandom.current().nextInt(0, 999999)
        return String.format("%06d", randomNumber)
    }
}