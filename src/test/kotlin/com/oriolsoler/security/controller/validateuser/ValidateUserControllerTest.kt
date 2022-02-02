package com.oriolsoler.security.controller.validateuser

import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.validateuser.ValidateUserException
import com.oriolsoler.security.application.validateuser.ValidateUserUseCase
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.VerificationException
import com.oriolsoler.security.domain.verification.VerificationExpiredException
import com.oriolsoler.security.domain.verification.VerificationAlreadyVerifiedException
import com.oriolsoler.security.infrastucutre.controller.validateuser.ValidateUserCommand
import com.oriolsoler.security.infrastucutre.controller.validateuser.ValidateUserController
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
class ValidateUserControllerTest {
    private lateinit var mockMvc: MockMvc
    private val validateUserUseCase = mock<ValidateUserUseCase> {}
    private val validateUserController = ValidateUserController(validateUserUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(validateUserController)
            .build()
    }

    @Test
    fun `should validate user`() {
        val user = User()
        val verification = "987456"
        val validateUserCommand = ValidateUserCommand(user.id.value.toString(), verification)
        doNothing().`when`(validateUserUseCase).execute(validateUserCommand)

        val response = validateUserController.verify(validateUserCommand)

        assertEquals(ACCEPTED, response.statusCode)
        verify(validateUserUseCase).execute(validateUserCommand)
    }

    @Test
    fun `should handle verification expired error`() {
        val error = VerificationExpiredException()
        val errorVerify = ValidateUserException(error.message, error)
        val response = validateUserController.handleVerificationError(errorVerify)

        assertEquals(GONE, response.statusCode)
        assertEquals("Validate user error: Verification expired", response.body)
    }

    @Test
    fun `should handle verification used error`() {
        val error = VerificationAlreadyVerifiedException()
        val errorVerify = ValidateUserException(error.message, error)
        val response = validateUserController.handleVerificationError(errorVerify)

        assertEquals(CONFLICT, response.statusCode)
        assertEquals("Validate user error: Verification already used", response.body)
    }

    @Test
    fun `should handle other errors`() {
        val errorVerify = ValidateUserException("Unknown error", VerificationException("any"))
        val response = validateUserController.handleVerificationError(errorVerify)

        assertEquals(SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals("Validate user error: Unknown error", response.body)
    }

    @Test
    fun `should handle verification not found`() {
        val error = VerificationNotFoundException()
        val errorVerify = ValidateUserException(error.message, error)
        val response = validateUserController.handleVerificationError(errorVerify)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Validate user error: No verification found", response.body)
    }

    @Test
    fun `should handle user not found`() {
        val error = UserNotFoundException()
        val errorVerify = ValidateUserException(error.message, error)
        val response = validateUserController.handleVerificationError(errorVerify)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Validate user error: User not found", response.body)
    }
}