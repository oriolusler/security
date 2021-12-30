package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class JaspyTestCase {

    @Value("\${jasypt.test}")
    private lateinit var encryptedProperty: String

    @Test
    fun `should read encrypted properties`() {
        val expectedString = "hola"
        assertEquals(expectedString, encryptedProperty)
    }
}