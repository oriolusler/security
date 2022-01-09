package com.oriolsoler.security.controller.accessverification

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.forgotpassword.UpdatePasswordException
import com.oriolsoler.security.application.forgotpassword.UpdatePasswordUseCase
import com.oriolsoler.security.domain.verification.VerificationNotVerifiedException
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.UpdatePasswordController
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.UpdatePasswordRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class UpdatePasswordControllerTest {
    private lateinit var mockMvc: MockMvc
    private val updatePasswordUseCase = mock<UpdatePasswordUseCase> {}
    private val updatePasswordController = UpdatePasswordController(updatePasswordUseCase)

    private val email = "email@online.com"

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(updatePasswordController)
            .setControllerAdvice(updatePasswordController)
            .build()
    }

    @Test
    fun `should send email to refresh password`() {
        val forgotPasswordCommand = UpdatePasswordRequestCommand(email, "798349", "NEW_PASSWORD")

        doNothing().`when`(updatePasswordUseCase).execute(forgotPasswordCommand)

        val response = updatePasswordController.update(forgotPasswordCommand)

        assertEquals(OK, response.statusCode)
        verify(updatePasswordUseCase, times(1)).execute(forgotPasswordCommand)
    }

    @Test
    fun `should handle error if user not found`() {
        val userNotFoundError = UserNotFoundException()
        val updatePasswordException = UpdatePasswordException(userNotFoundError.message, userNotFoundError)

        val response = updatePasswordController.handleUpdatePasswordError(updatePasswordException)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Update password error: User not found", response.body)
    }

    @Test
    fun `should handle error if verification not found`() {
        val userNotFoundError = VerificationNotFoundException()
        val updatePasswordException = UpdatePasswordException(userNotFoundError.message, userNotFoundError)

        val response = updatePasswordController.handleUpdatePasswordError(updatePasswordException)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Update password error: No verification found", response.body)
    }

    @Test
    fun `should handle error if verification not validated`() {
        val userNotFoundError = VerificationNotVerifiedException()
        val updatePasswordException = UpdatePasswordException(userNotFoundError.message, userNotFoundError)

        val response = updatePasswordController.handleUpdatePasswordError(updatePasswordException)

        assertEquals(UNAUTHORIZED, response.statusCode)
        assertEquals("Update password error: Verification has not been verified", response.body)
    }

    @Test
    fun `should handle any error`() {
        val anyException = Exception("Any error")
        val updatePasswordException = UpdatePasswordException(anyException.message, anyException)

        val response = updatePasswordController.handleUpdatePasswordError(updatePasswordException)

        assertEquals(SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals("Update password error: Any error", response.body)
    }
}

