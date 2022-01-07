package com.oriolsoler.security.domain.services

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.domain.services.exceptions.InvalidPasswordException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpringEncoderPasswordServiceTest {
    @Test
    fun `should encrypt password`() {
        val passwordRaw = "hello123"
        val passwordEncrypted = "EncryptedPassword"
        val passwordEncoder = mock<PasswordEncoder> {
            on { encode(passwordRaw) } doReturn passwordEncrypted
        }
        val passwordService = SpringEncoderPasswordService(passwordEncoder)
        val encryptedPasswordResult = passwordService.encode(passwordRaw)
        assertEquals(passwordEncrypted, encryptedPasswordResult)
    }

    @Test
    fun `should match passwords`() {
        val passwordRaw = "hello123"
        val passwordEncrypted = "EncryptedPassword"
        val passwordEncoder = mock<PasswordEncoder> {
            on { matches(passwordRaw, passwordEncrypted) } doReturn true
        }
        val passwordService = SpringEncoderPasswordService(passwordEncoder)
        passwordService.matches(passwordRaw, passwordEncrypted)
        assertTrue { true }
    }

    @Test
    fun `should throw exception if password does not match`() {
        val passwordRaw = "hello123"
        val passwordEncrypted = "EncryptedPassword"
        val passwordEncoder = mock<PasswordEncoder> {
            on { matches(passwordRaw, passwordEncrypted) } doReturn false
        }

        val passwordService = SpringEncoderPasswordService(passwordEncoder)
        val exception = assertThrows<InvalidPasswordException> {
            passwordService.matches(passwordRaw, passwordEncrypted)
        }
        assertEquals("Invalid password", exception.message)
    }
}