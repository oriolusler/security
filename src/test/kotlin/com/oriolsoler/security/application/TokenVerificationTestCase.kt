package com.oriolsoler.security.application


import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.accessVerification.AccessVerificationException
import com.oriolsoler.security.application.accessVerification.TokenVerification
import com.oriolsoler.security.application.accessVerification.AccessVerificationUseCase
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.accessVerification.AccessVerificationCommand
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TokenVerificationTestCase {

    @Test
    fun `should verify access given a token`() {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJmMzA0Yzc0ZC05MDI3LTQ0N2ItYTJmOC01Y2I4N2IzNTQ4ZTEifQ" +
                ".SF8D4I7YFUX0DreosszjU83S1zk58zExq0_AYV5O3ooYw1Q-f_pE8NM2tNdxESgKz9RZq8cguViX2NdYNR6fTQ"
        val user = User(locked = false)

        val tokenVerification = mock<TokenVerification> {
            on { validate(token) } doReturn user.id.value.toString()
        }
        val userRepository = mock<UserRepository> {
            on { getBy(user.id) } doReturn user
        }

        val accessVerificationUseCase = AccessVerificationUseCase(tokenVerification, userRepository)
        val accessVerificationCommand = AccessVerificationCommand(token)

        val accessResult = accessVerificationUseCase.execute(accessVerificationCommand)

        assertTrue(accessResult)
        verify(tokenVerification, times(1)).validate(token)
        verify(userRepository, times(1)).getBy(user.id)
    }

    @Test
    fun `should throw error if user is locked`() {
        val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJmMzA0Yzc0ZC05MDI3LTQ0N2ItYTJmOC01Y2I4N2IzNTQ4ZTEifQ" +
                ".SF8D4I7YFUX0DreosszjU83S1zk58zExq0_AYV5O3ooYw1Q-f_pE8NM2tNdxESgKz9RZq8cguViX2NdYNR6fTQ"
        val user = User(locked = true)

        val tokenVerification = mock<TokenVerification> {
            on { validate(token) } doReturn user.id.value.toString()
        }
        val userRepository = mock<UserRepository> {
            on { getBy(user.id) } doReturn user
        }

        val accessVerificationUseCase = AccessVerificationUseCase(tokenVerification, userRepository)
        val accessVerificationCommand = AccessVerificationCommand(token)

        val response = assertThrows<AccessVerificationException> {
            accessVerificationUseCase.execute(accessVerificationCommand)
        }

        assertEquals("Access verification error: User locked", response.message)
    }
}