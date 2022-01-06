package com.oriolsoler.security.controller.accessverification

import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.forgotpassword.ForgotPasswordUseCase
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.ForgotPasswordController
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.ForgotPasswordRequestCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus.ACCEPTED
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
}