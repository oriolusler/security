package com.oriolsoler.security.controller.verifyverification

import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.validateverification.VerifyException
import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.VerificationException
import com.oriolsoler.security.domain.verification.VerificationExpiredException
import com.oriolsoler.security.domain.verification.VerificationUsedException
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationController
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
class VerifyVerificationControllerTest {
    private lateinit var mockMvc: MockMvc
    private val verifyVerificationUseCase = mock<VerifyVerificationUseCase> {}
    private val verifyVerificationController = VerifyVerificationController(verifyVerificationUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(verifyVerificationController)
            .build()
    }

    @Test
    fun `should create user`() {
        val user = User()
        val verification = "987456"
        val verifyVerificationCommand = VerifyVerificationCommand(user.id.value.toString(), verification)
        doNothing().`when`(verifyVerificationUseCase).execute(verifyVerificationCommand)

        val response = verifyVerificationController.verify(verifyVerificationCommand)

        assertEquals(ACCEPTED, response.statusCode)
        verify(verifyVerificationUseCase, times(1)).execute(verifyVerificationCommand)
    }

    @Test
    fun `should handle expired error`() {
        val error = VerificationExpiredException()
        val errorVerify = VerifyException(error.message, error)
        val response = verifyVerificationController.handleVerificationError(errorVerify)

        assertEquals(GONE, response.statusCode)
        assertEquals("Verify error: Verification expired", response.body)
    }

    @Test
    fun `should handle used error`() {
        val error = VerificationUsedException()
        val errorVerify = VerifyException(error.message, error)
        val response = verifyVerificationController.handleVerificationError(errorVerify)

        assertEquals(CONFLICT, response.statusCode)
        assertEquals("Verify error: Verification already used", response.body)
    }

    @Test
    fun `should handle other errors`() {
        val errorVerify = VerifyException("Unknown error", VerificationException("any"))
        val response = verifyVerificationController.handleVerificationError(errorVerify)

        assertEquals(SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals("Verify error: Unknown error", response.body)
    }

    @Test
    fun `should handle validation not found`() {
        val error = VerificationNotFoundException()
        val errorVerify = VerifyException(error.message, error)
        val response = verifyVerificationController.handleVerificationError(errorVerify)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Verify error: No verification found", response.body)
    }

    @Test
    fun `should handle user not found`() {
        val error = UserNotFoundException()
        val errorVerify = VerifyException(error.message, error)
        val response = verifyVerificationController.handleVerificationError(errorVerify)

        assertEquals(NOT_FOUND, response.statusCode)
        assertEquals("Verify error: User not found", response.body)
    }
}