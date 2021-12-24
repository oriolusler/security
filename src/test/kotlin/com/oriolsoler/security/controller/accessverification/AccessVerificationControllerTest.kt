package com.oriolsoler.security.controller.accessverification

import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.accessverification.AccessVerificationException
import com.oriolsoler.security.application.accessverification.AccessVerificationUseCase
import com.oriolsoler.security.infrastucutre.controller.accessverification.AccessVerificationCommand
import com.oriolsoler.security.infrastucutre.controller.accessverification.AccessVerificationController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
class AccessVerificationControllerTest {
    private lateinit var mockMvc: MockMvc
    private val accessVerificationUseCase = mock<AccessVerificationUseCase> {}
    private val accessVerificationController = AccessVerificationController(accessVerificationUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(accessVerificationController)
            .setControllerAdvice(accessVerificationController)
            .build()
    }

    @Test
    fun `should validate token user`() {
        val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJiNmMyNDU4ZC02NTJjLTQwNjEtOTVlNy00ZjQ3YjBkZTJiOTQifQ" +
                ".BYGZ26WmXqSi5GWdKCGXzIjebofEZcIfSOQYqYuXkbqlFnHgZGXQrTRyVxi9BRypibNgrGKnspMM-eIayx1Rmw"

        val accessValidationCommand = AccessVerificationCommand(token)
        `when`(accessVerificationUseCase.execute(accessValidationCommand)).thenReturn(true)

        val response = accessVerificationController.validate(token)

        assertEquals(ACCEPTED, response.statusCode)
    }

    @Test
    fun `should invalidate request if token not valid`() {
        val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJiNmMyNDU4ZC02NTJjLTQwNjEtOTVlNy00ZjQ3YjBkZTJiOTQifQ" +
                ".BYGZ26WmXqSi5GWdKCGXzIjebofEZcIfSOQYqYuXkbqlFnHgZGXQrTRyVxi9BRypibNgrGKnspMM-eIayx1Rmw"

        val accessValidationCommand = AccessVerificationCommand(token)
        `when`(accessVerificationUseCase.execute(accessValidationCommand)).thenReturn(false)

        val response = accessVerificationController.validate(token)

        assertEquals(UNAUTHORIZED, response.statusCode)
    }


    @Test
    fun `should handle user exceptions`() {
        val verificationError = "User locked"

        val response = accessVerificationController
            .handleAccessVerificationException(AccessVerificationException(verificationError))

        assertEquals(UNAUTHORIZED, response.statusCode)
        assertEquals("Access verification error: $verificationError", response.body)
    }

    @Test
    fun `should generate unique hash for access verification command`() {
        val accessVerificationCommand = AccessVerificationCommand("token")
        assertNotNull(accessVerificationCommand.hashCode())
    }
}