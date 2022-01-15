package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.verification.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class VerificationTest {
    @Test
    fun `should throw exception if used`() {
        val verification = Verification(verification = "489324", validated = true)
        val exception = assertThrows<VerificationAlreadyVerifiedException> { verification.checkIfAlreadyValidated() }
        assertEquals("Verification already used", exception.message)
    }

    @Test
    fun `should throw exception if not used`() {
        val verification = Verification(verification = "489324", validated = false)
        val exception = assertThrows<VerificationNotVerifiedException> { verification.checkIfNotValidated() }
        assertEquals("Verification has not been verified", exception.message)
    }

    @Test
    fun `should throw exception if is not usable`() {
        val verification = Verification(verification = "489324", validated = true, usable = false)
        val exception = assertThrows<VerificationNotUsableException> { verification.checkIfUsable() }
        assertEquals("Verification not usable", exception.message)
    }

    @Test
    fun `should throw exception if not valid`() {
        val verification = Verification(verification = "489324", validated = false)
        val exception = assertThrows<VerificationExpiredException> {
            verification.checkIfExpired(
                LocalDateTime.now().plusDays(1)
            )
        }
        assertEquals("Verification expired", exception.message)
    }
}