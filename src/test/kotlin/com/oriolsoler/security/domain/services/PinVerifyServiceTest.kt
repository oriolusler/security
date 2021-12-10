package com.oriolsoler.security.domain.services

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        val result = pinVerifyService.generate()

        assertNotNull(result)
        assertEquals(6, result.verification.length)
        assertTrue { StringUtils.isNumeric(result.verification) }
    }

    @Test
    fun `should create validation valid for x minutes`() {
        val result = pinVerifyService.generate()

        assertNotNull(result)
        assertEquals(now, result.creationDate)
        assertEquals(now.plusMinutes(minutesValid), result.expirationDate)
    }

    @Test
    fun `should return true if pin is valid`() {
        val user = User()
        val verification = Verification(verification = "856423")
        val userVerification = UserVerification(user, verification)

        val result = pinVerifyService.isValid(userVerification)

        assertTrue { result }
        verify(clock, times(1)).now()
    }

    @Test
    fun `should return false if pin is valid`() {
        val user = User()
        val verification = Verification(verification = "543534")
        val userVerification = UserVerification(user, verification)

        val clock = mock<ClockService> {
            on { now() } doReturn now.plusMinutes(30L)
        }
        pinVerifyService = PinVerifyService(clock, minutesValid)

        val result = pinVerifyService.isValid(userVerification)

        assertFalse { result }
        verify(clock, times(1)).now()
    }

    @Test
    fun `should return false if pin is already used`() {
        val user = User()
        val verification = Verification(verification = "923876", used = true)
        val userVerification = UserVerification(user, verification)

        val result = pinVerifyService.isValid(userVerification)

        assertFalse { result }
    }
}