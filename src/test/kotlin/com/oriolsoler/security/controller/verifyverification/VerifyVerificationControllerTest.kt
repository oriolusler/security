package com.oriolsoler.security.controller.verifyverification

import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.ACCEPTED
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
}