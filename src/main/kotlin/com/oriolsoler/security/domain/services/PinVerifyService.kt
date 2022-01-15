package com.oriolsoler.security.domain.services

import com.oriolsoler.security.application.VerifyService
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

    override fun checkIfExpired(verification: Verification) {
        verification.checkIfExpired(clock.now())
    }

    override fun checkIfAlreadyValidated(verification: Verification) {
        verification.checkIfAlreadyValidated()
    }

    override fun checkIfNotValidated(verification: Verification) {
        verification.checkIfNotValidated()
    }

    override fun checkIfUsable(verification: Verification) {
        verification.checkIfUsable()
    }

    private fun generateVerificationCode(): String {
        val randomNumber = ThreadLocalRandom.current().nextInt(0, 999999)
        return String.format("%06d", randomNumber)
    }
}