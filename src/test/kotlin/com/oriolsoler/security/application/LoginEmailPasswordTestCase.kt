package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.LoginException
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.services.exceptions.InvalidPasswordException
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import org.junit.jupiter.api.Test
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

        val passwordService = mock<PasswordService> { }
        doNothing().`when`(passwordService).matches(password, encryptedPassword)

        val token = Token("extremely_protected_access_jwt", "extremely_protected_refresh_jwt")
        val tokenGenerator = mock<TokenGenerator> {
            on { generate(any()) } doReturn token
        }

        val loginEmailPasswordTestCase = LoginEmailPasswordUseCase(
            userRepository,
            passwordService,
            tokenGenerator
        )

        val loginRequestCommand = LoginRequestCommand(email, password)
        val loginResponse = loginEmailPasswordTestCase.execute(loginRequestCommand)

        assertNotNull(loginResponse)
        assertNotNull(loginResponse.token)
        assertTrue { loginResponse.token.accessToken.isNotEmpty() }
        assertTrue { loginResponse.token.refreshToken.isNotEmpty() }
        assertEquals(user.id.value.toString(), loginResponse.user.id)
        assertEquals(email, loginResponse.user.email)

        verify(passwordService).matches(password, encryptedPassword)
        verify(userRepository).getBy(email)
        verify(tokenGenerator).generate(any())
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

        val passwordService = mock<PasswordService> {}
        given { passwordService.matches(password, encryptedPassword) } willAnswer { throw InvalidPasswordException() }

        val token = Token("extremely_protected_access_jwt", "extremely_protected_refresh_jwt")
        val tokenGenerator = mock<TokenGenerator> {
            on { generate(any()) } doReturn token
        }

        val loginEmailPasswordTestCase = LoginEmailPasswordUseCase(userRepository, passwordService, tokenGenerator)

        val loginRequestCommand = LoginRequestCommand(email, password)
        val exception = assertFailsWith<LoginException> { loginEmailPasswordTestCase.execute(loginRequestCommand) }

        assertEquals("Login error: Invalid password", exception.message)
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

        val passwordService = mock<PasswordService> {}
        doNothing().`when`(passwordService).matches(password, encryptedPassword)

        val tokenGenerator = mock<TokenGenerator> {
            on { generate(any()) } doReturn Token(
                "extremely_protected_access_jwt",
                "extremely_protected_refresh_jwt"
            )
        }

        val loginEmailPasswordTestCase = LoginEmailPasswordUseCase(userRepository, passwordService, tokenGenerator)

        val loginRequestCommand = LoginRequestCommand(email, password)
        val exception = assertFailsWith<LoginException> { loginEmailPasswordTestCase.execute(loginRequestCommand) }

        assertEquals("Login error: User locked", exception.message)
    }

    @Test
    fun `login with unregistered user`() {
        val email = "unregistred@email.com"
        val password = "password"

        val userRepository = mock<UserRepository> {}
        given { userRepository.getBy(email) } willAnswer { throw UserNotFoundException() }

        val passwordService = mock<PasswordService> {}
        val tokenGenerator = mock<TokenGenerator> {}
        val loginEmailPasswordTestCase = LoginEmailPasswordUseCase(userRepository, passwordService, tokenGenerator)

        val loginRequestCommand = LoginRequestCommand(email, password)
        val exception = assertFailsWith<LoginException> { loginEmailPasswordTestCase.execute(loginRequestCommand) }

        assertEquals("Login error: User not found", exception.message)
    }
}