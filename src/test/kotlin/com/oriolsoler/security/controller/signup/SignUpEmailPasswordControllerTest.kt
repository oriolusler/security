package com.oriolsoler.security.controller.signup

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.SignUpException
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserAlreadyExistsException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.CREATED
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

        val user = User(email = email)
        `when`(signUpEmailPasswordUseCase.execute(signupRequestCommand)).thenReturn(user)

        val response = signUpEmailPasswordController.register(signupRequestCommand)

        assertEquals(CREATED, response.statusCode)
        assertEquals("User with email $email created successfully", response.body)
        verify(signUpEmailPasswordUseCase, times(1)).execute(signupRequestCommand)
    }

    @Test
    fun `should handle error if user already exists`() {
        val userAlreadyExistsException = UserAlreadyExistsException()
        val signUpException = SignUpException(userAlreadyExistsException.message, userAlreadyExistsException)
        val response = signUpEmailPasswordController.handleSignUpError(signUpException)

        assertEquals(CONFLICT, response.statusCode)
        assertEquals(signUpException.message, response.body)
    }
}