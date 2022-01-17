package com.oriolsoler.security.domain.services

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.domain.verification.VerificationType.*
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PinVerifyServiceTest {

    private val now = ClockService().now()
    private val minutesValid = 15L
    private lateinit var pinVerifyService: PinVerifyService
    private lateinit var clock: ClockService

    @BeforeEach
    fun setUp() {
        clock = mock {
            on { now() } doReturn now
        }

        pinVerifyService = PinVerifyService(clock, minutesValid)
    }

    @Test
    fun `should generate active pin given user`() {
        val result = pinVerifyService.generate(VALIDATE_USER)

        assertNotNull(result)
        assertEquals(6, result.verification.length)
        assertTrue { StringUtils.isNumeric(result.verification) }
    }

    @Test
    fun `should create validation valid for x minutes`() {
        val result = pinVerifyService.generate(VALIDATE_USER)

        assertNotNull(result)
        assertEquals(now, result.creationDate)
        assertEquals(now.plusMinutes(minutesValid), result.expirationDate)
    }

    @Test
    fun `should return true if pin is valid`() {
        val user = User()
        val verification = Verification(verification = "856423", type = VALIDATE_USER)
        val userVerification = UserVerification(user, verification)

        pinVerifyService.checkIfExpired(userVerification.verification)

        assertTrue { true }
        verify(clock, times(1)).now()
    }

    @Test
    fun `should return exception if pin is not valid`() {
        val user = User()
        val verification = Verification(verification = "543534", type = VALIDATE_USER)
        val userVerification = UserVerification(user, verification)

        val clock = mock<ClockService> {
            on { now() } doReturn now.plusMinutes(30L)
        }
        pinVerifyService = PinVerifyService(clock, minutesValid)

        val throws = assertThrows<VerificationExpiredException> {
            pinVerifyService.checkIfExpired(userVerification.verification)
        }
        assertEquals("Verification expired", throws.message)
        verify(clock, times(1)).now()
    }

    @Test
    fun `should return exception if pin is already used`() {
        val user = User()
        val verification = Verification(verification = "923876", validated = true, type = VALIDATE_USER)
        val userVerification = UserVerification(user, verification)

        val throws = assertThrows<VerificationAlreadyVerifiedException> {
            pinVerifyService.checkIfAlreadyValidated(userVerification.verification)
        }
        assertEquals("Verification already used", throws.message)
    }

    @Test
    fun `should return exception if pin is not used`() {
        val user = User()
        val verification = Verification(verification = "923876", validated = false, type = VALIDATE_USER)
        val userVerification = UserVerification(user, verification)

        val throws = assertThrows<VerificationNotVerifiedException> {
            pinVerifyService.checkIfNotValidated(userVerification.verification)
        }
        assertEquals("Verification has not been verified", throws.message)
    }

    @Test
    fun `should return exception if pin is not usable`() {
        val user = User()
        val verification = Verification(verification = "923876", validated = false, usable = false, type = VALIDATE_USER)
        val userVerification = UserVerification(user, verification)

        val throws = assertThrows<VerificationNotUsableException> {
            pinVerifyService.checkIfUsable(userVerification.verification)
        }
        assertEquals("Verification not usable", throws.message)
    }
}