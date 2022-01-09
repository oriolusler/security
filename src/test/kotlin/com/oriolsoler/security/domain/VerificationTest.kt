package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.verification.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals

class VerificationTest {
    @Test
    fun `should throw exception if used`() {
        val verification = Verification(verification = "489324", used = true)
        val exception = assertThrows<VerificationUsedException> { verification.validateIfUsed() }
        assertEquals("Verification already used", exception.message)
    }

    @Test
    fun `should throw exception if not used`() {
        val verification = Verification(verification = "489324", used = false)
        val exception = assertThrows<VerificationNotVerifiedException> { verification.validateIfNotUsed() }
        assertEquals("Verification has not been verified", exception.message)
    }

    @Test
    fun `should throw exception if deleted`() {
        val verification = Verification(verification = "489324", used = true, deleted = true)
        val exception = assertThrows<VerificationDeletedException> { verification.validateIfDeleted() }
        assertEquals("Verification deleted", exception.message)
    }

    @Test
    fun `should throw exception if not valid`() {
        val verification = Verification(verification = "489324", used = false)
        val exception = assertThrows<VerificationExpiredException> {
            verification.validateIfExpired(
                LocalDateTime.now().plusDays(1)
            )
        }
        assertEquals("Verification expired", exception.message)
    }
}