package com.oriolsoler.security.domain.services

import com.oriolsoler.security.application.signup.VerifyService
import java.util.concurrent.ThreadLocalRandom

class PinVerifyService : VerifyService {
    override fun generate(): String {
        val randomNumber = ThreadLocalRandom.current().nextInt(0, 999999)
        return String.format("%06d", randomNumber)
    }
}