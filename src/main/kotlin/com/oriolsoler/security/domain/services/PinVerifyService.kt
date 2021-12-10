package com.oriolsoler.security.domain.services

import com.oriolsoler.security.application.signup.VerifyService
import com.oriolsoler.security.domain.Verification
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

    private fun generateVerificationCode(): String {
        val randomNumber = ThreadLocalRandom.current().nextInt(0, 999999)
        return String.format("%06d", randomNumber)
    }
}