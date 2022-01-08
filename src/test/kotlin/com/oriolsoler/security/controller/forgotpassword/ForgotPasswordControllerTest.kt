package com.oriolsoler.security.controller.accessverification

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.forgotpassword.ForgotPasswordException
import com.oriolsoler.security.application.forgotpassword.ForgotPasswordUseCase
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.ForgotPasswordController
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.ForgotPasswordRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class ForgotPasswordControllerTest {
    private lateinit var mockMvc: MockMvc
    private val forgotPasswordUserCase = mock<ForgotPasswordUseCase> {}
    private val forgotPasswordController = ForgotPasswordController(forgotPasswordUserCase)

    private val email = "email@online.com"

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(forgotPasswordController)
            .setControllerAdvice(forgotPasswordController)
            .build()
    }

    @Test
    fun `should send email to refresh password`() {
        val forgotPasswordCommand = ForgotPasswordRequestCommand(email)

        doNothing().`when`(forgotPasswordUserCase).execute(forgotPasswordCommand)

        val response = forgotPasswordController.forgot(forgotPasswordCommand)

        assertEquals(ACCEPTED, response.statusCode)
        verify(forgotPasswordUserCase, times(1)).execute(forgotPasswordCommand)
    }

    @Test
    fun `should handle error if user not found`() {
        val userNotFoundError = UserNotFoundException()
        val forgotPasswordException = ForgotPasswordException(userNotFoundError.message, userNotFoundError)

        val response = forgotPasswordController.handleForgotPasswordError(forgotPasswordException)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Forgot password error: User not found", response.body)
    }

    @Test
    fun `should handle any error`() {
        val anyException = Exception("Any error")
        val forgotPasswordException = ForgotPasswordException(anyException.message, anyException)

        val response = forgotPasswordController.handleForgotPasswordError(forgotPasswordException)

        assertEquals(SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals("Forgot password error: Any error", response.body)
    }
}

