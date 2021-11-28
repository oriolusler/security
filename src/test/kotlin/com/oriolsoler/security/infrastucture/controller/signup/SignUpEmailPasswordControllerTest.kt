package com.oriolsoler.security.infrastucture.controller.signup

import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus.OK
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
class SignUpEmailPasswordControllerTest {
    private lateinit var mockMvc: MockMvc
    private val signUpEmailPasswordUseCase = mock<SignUpEmailPasswordUseCase> {}
    private val signUpEmailPasswordController = SignUpEmailPasswordController(signUpEmailPasswordUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(signUpEmailPasswordController)
            .build()
    }

    @Test
    fun `should create user`() {
        val email = "user@email.com"
        val password = "password"
        val signupRequestCommand = SignUpRequestCommand(email, password)

        val user = User(email)
        `when`(signUpEmailPasswordUseCase.execute(signupRequestCommand)).thenReturn(user)

        val response = signUpEmailPasswordController.register(signupRequestCommand)

        assertEquals(OK, response.statusCode)
        assertEquals("User with email $email created successfully", response.body)
    }
}