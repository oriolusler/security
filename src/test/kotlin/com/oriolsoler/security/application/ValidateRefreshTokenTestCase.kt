package com.oriolsoler.security.application

import com.auth0.jwt.exceptions.JWTVerificationException
import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.accessverification.TokenVerification
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenException
import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenUseCase
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.validaterefreshtoken.ValidateRefreshTokenCommand
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ValidateRefreshTokenTestCase {

    @Test
    fun `should validate refresh token`() {
        val refreshToken = "Secure_refresh_token"
        val user = User(email = "email@online.com", locked = false)
        val validateRefreshTokenCommand = ValidateRefreshTokenCommand(refreshToken = refreshToken)

        val tokenVerification = mock<TokenVerification> {
            on { validateRefreshToken(refreshToken) } doReturn user.id.value.toString()
        }
        val userRepository = mock<UserRepository> {
            on { getBy(user.id) } doReturn user
        }
        val tokenGenerator = mock<TokenGenerator> {
            on { generate(any()) } doReturn Token(
                "extremely_protected_access_jwt",
                "extremely_protected_refresh_jwt"
            )
        }

        val validateRefreshTokenUseCase = ValidateRefreshTokenUseCase(
            tokenVerification,
            userRepository,
            tokenGenerator
        )

        val response = validateRefreshTokenUseCase.execute(validateRefreshTokenCommand)

        assertNotNull(response.accessToken)
        assertNotNull(response.refreshToken)
        verify(tokenVerification, times(1)).validateRefreshToken(refreshToken)
        verify(userRepository, times(1)).getBy(user.id)
        verify(tokenGenerator, times(1)).generate(user.id)
    }

    @Test
    fun `should return error if user is not valid`() {
        val refreshToken = "Secure_refresh_token"
        val user = User(email = "email@online.com", locked = true)
        val validateRefreshTokenCommand = ValidateRefreshTokenCommand(refreshToken = refreshToken)

        val tokenVerification = mock<TokenVerification> {
            on { validateRefreshToken(refreshToken) } doReturn user.id.value.toString()
        }
        val userRepository = mock<UserRepository> {
            on { getBy(user.id) } doReturn user
        }
        val tokenGenerator = mock<TokenGenerator> { }

        val validateRefreshTokenUseCase = ValidateRefreshTokenUseCase(
            tokenVerification,
            userRepository,
            tokenGenerator
        )

        val exception = assertThrows<ValidateRefreshTokenException> {
            validateRefreshTokenUseCase.execute(validateRefreshTokenCommand)
        }

        assertEquals("Refresh token validation error: User locked", exception.message)
    }

    @Test
    fun `should return error if refresh token is not valid`() {
        val refreshToken = "Secure_refresh_token"
        val user = User(email = "email@online.com", locked = false)
        val validateRefreshTokenCommand = ValidateRefreshTokenCommand(refreshToken = refreshToken)

        val tokenVerification = mock<TokenVerification> { }
        given { tokenVerification.validateRefreshToken(refreshToken) } willAnswer { throw JWTVerificationException("Error with refresh token validation") }

        val userRepository = mock<UserRepository> { }
        val tokenGenerator = mock<TokenGenerator> { }

        val validateRefreshTokenUseCase = ValidateRefreshTokenUseCase(
            tokenVerification,
            userRepository,
            tokenGenerator
        )

        val exception = assertThrows<ValidateRefreshTokenException> {
            validateRefreshTokenUseCase.execute(validateRefreshTokenCommand)
        }

        assertEquals("Refresh token validation error: Error with refresh token validation", exception.message)
    }

}