package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class LoginEmailPasswordTestCase {

    @Test
    fun `login with email and password`() {
        val email = "user@email.com"
        val password = "password"
        val encryptedPassword = "encrypted_password"
        val user = User(email = email, password = encryptedPassword, locked = false)

        val userRepository = mock<UserRepository> {
            on { getBy(email) } doReturn user
        }

        val passwordEncoder = mock<PasswordEncoder> {
            on { matches(password, encryptedPassword) } doReturn true
        }

        val token = Token("extremely_protected_jwt", "Bearer")
        val tokenGenerator = mock<TokenGenerator> {
            on { generate(any()) } doReturn token
        }

        val loginEmailPasswordTestCase = LoginEmailPasswordUseCase(
            userRepository,
            passwordEncoder,
            tokenGenerator
        )

        val loginRequestCommand = LoginRequestCommand(email, password)
        val loginResponse = loginEmailPasswordTestCase.execute(loginRequestCommand)

        assertNotNull(loginResponse)
        assertNotNull(loginResponse.token)
        assertTrue { loginResponse.token.isNotEmpty() }
        assertEquals("Bearer", loginResponse.type)
        assertEquals(user.id.value, loginResponse.id.value)
        assertEquals(email, loginResponse.email)

        verify(passwordEncoder, times(1)).matches(password, encryptedPassword)
        verify(userRepository, times(1)).getBy(email)
        verify(tokenGenerator, times(1)).generate(any())
    }

    @Test
    fun `login up with email and invalid password`() {
        val email = "user@email.com"
        val password = "invalid_password"
        val encryptedPassword = "encrypted_password"
        val user = User(email = email, password = encryptedPassword, locked = false)

        val userRepository = mock<UserRepository> {
            on { getBy(email) } doReturn user
        }

        val passwordEncoder = mock<PasswordEncoder> {
            on { matches(password, encryptedPassword) } doReturn false
        }

        val token = Token("extremely_protected_jwt", "Bearer")
        val tokenGenerator = mock<TokenGenerator> {
            on { generate(any()) } doReturn token
        }

        val loginEmailPasswordTestCase = LoginEmailPasswordUseCase(
            userRepository,
            passwordEncoder,
            tokenGenerator
        )

        val loginRequestCommand = LoginRequestCommand(email, password)
        val exception = assertFailsWith<RuntimeException> { loginEmailPasswordTestCase.execute(loginRequestCommand) }
        assertEquals("Invalid password", exception.message)
    }

    @Test
    fun `login with locked user`() {
        val email = "user@email.com"
        val password = "password"
        val encryptedPassword = "encrypted_password"
        val user = User(email = email, password = encryptedPassword, locked = true)

        val userRepository = mock<UserRepository> {
            on { getBy(email) } doReturn user
        }

        val passwordEncoder = mock<PasswordEncoder> {
            on { matches(password, encryptedPassword) } doReturn true
        }

        val tokenGenerator = mock<TokenGenerator> {
            on { generate(any()) } doReturn Token("extremely_protected_jwt", "Bearer")
        }

        val loginEmailPasswordTestCase = LoginEmailPasswordUseCase(
            userRepository,
            passwordEncoder,
            tokenGenerator
        )

        val loginRequestCommand = LoginRequestCommand(email, password)
        val exception = assertFailsWith<RuntimeException> { loginEmailPasswordTestCase.execute(loginRequestCommand) }
        assertEquals("User locked", exception.message)
    }
}