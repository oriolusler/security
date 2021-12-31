package com.oriolsoler.security.domain.services

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClockServiceTest {

    @Test
    fun `should generate current date time`() {
        val clockService = ClockService()

        val result = clockService.now()

        assertNotNull(result)
        assertTrue { result is LocalDateTime }
    }

    @Test
    fun `should generate current date`() {
        val clockService = ClockService()

        val result = clockService.nowDate()

        assertNotNull(result)
        assertTrue { result is Date }
    }

}