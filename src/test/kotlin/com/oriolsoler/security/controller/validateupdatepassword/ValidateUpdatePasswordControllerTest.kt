package com.oriolsoler.security.controller.validateupdatepassword

import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.validateupdatepassword.ValidateUpdatePasswordException
import com.oriolsoler.security.application.validateupdatepassword.ValidateUpdatePasswordUseCase
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.VerificationException
import com.oriolsoler.security.domain.verification.VerificationExpiredException
import com.oriolsoler.security.domain.verification.VerificationAlreadyVerifiedException
import com.oriolsoler.security.infrastucutre.controller.validateupdatepassword.ValidateUpdatePasswordController
import com.oriolsoler.security.infrastucutre.controller.validateupdatepassword.ValidateUpdatedPasswordCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.GONE
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class ValidateUpdatePasswordControllerTest {
    private lateinit var mockMvc: MockMvc
    private val validateUpdatePasswordUseCase = mock<ValidateUpdatePasswordUseCase> {}
    private val validateUpdatePasswordController = ValidateUpdatePasswordController(validateUpdatePasswordUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(validateUpdatePasswordController)
            .build()
    }

    @Test
    fun `should validate update password`() {
        val user = User()
        val verification = "987456"
        val validateUpdatedPasswordCommand = ValidateUpdatedPasswordCommand(user.id.value.toString(), verification)
        doNothing().`when`(validateUpdatePasswordUseCase).execute(validateUpdatedPasswordCommand)

        val response = validateUpdatePasswordController.verify(validateUpdatedPasswordCommand)

        assertEquals(ACCEPTED, response.statusCode)
        verify(validateUpdatePasswordUseCase).execute(validateUpdatedPasswordCommand)
    }

    @Test
    fun `should handle expired error`() {
        val error = VerificationExpiredException()
        val errorVerify = ValidateUpdatePasswordException(error.message, error)
        val response = validateUpdatePasswordController.handleVerificationError(errorVerify)

        assertEquals(GONE, response.statusCode)
        assertEquals("Validate update password error: Verification expired", response.body)
    }

    @Test
    fun `should handle used error`() {
        val error = VerificationAlreadyVerifiedException()
        val errorVerify = ValidateUpdatePasswordException(error.message, error)
        val response = validateUpdatePasswordController.handleVerificationError(errorVerify)

        assertEquals(CONFLICT, response.statusCode)
        assertEquals("Validate update password error: Verification already used", response.body)
    }

    @Test
    fun `should handle other errors`() {
        val errorVerify = ValidateUpdatePasswordException("Unknown error", VerificationException("any"))
        val response = validateUpdatePasswordController.handleVerificationError(errorVerify)

        assertEquals(SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals("Validate update password error: Unknown error", response.body)
    }

    @Test
    fun `should handle validation not found`() {
        val error = VerificationNotFoundException()
        val errorVerify = ValidateUpdatePasswordException(error.message, error)
        val response = validateUpdatePasswordController.handleVerificationError(errorVerify)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Validate update password error: No verification found", response.body)
    }

    @Test
    fun `should handle user not found`() {
        val error = UserNotFoundException()
        val errorVerify = ValidateUpdatePasswordException(error.message, error)
        val response = validateUpdatePasswordController.handleVerificationError(errorVerify)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Validate update password error: User not found", response.body)
    }
}