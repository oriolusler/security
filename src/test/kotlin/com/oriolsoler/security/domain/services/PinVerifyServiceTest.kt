package com.oriolsoler.security.domain.services

import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PinVerifyServiceTest {

    @Test
    fun `should generate active pin given user`() {
        val pinVerifyService = PinVerifyService()

        val result = pinVerifyService.generate()

        assertNotNull(result)
        assertEquals(6, result.length)
        assertTrue { StringUtils.isNumeric(result) }
    }
}