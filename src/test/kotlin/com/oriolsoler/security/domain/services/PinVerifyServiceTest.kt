package com.oriolsoler.security.domain.services

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PinVerifyServiceTest {

    private val now = ClockService().now()
    private val minutesValid = 15L
    private lateinit var pinVerifyService: PinVerifyService

    @BeforeEach
    fun setUp() {
        val clock = mock<ClockService> {
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
}