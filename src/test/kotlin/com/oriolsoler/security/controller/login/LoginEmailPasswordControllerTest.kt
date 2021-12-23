package com.oriolsoler.security.controller.login

import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.LoginException
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.infrastucutre.controller.login.LoginEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
class LoginEmailPasswordControllerTest {
    private lateinit var mockMvc: MockMvc
    private val loginEmailPasswordUseCase = mock<LoginEmailPasswordUseCase> {}
    private val loginEmailPasswordController = LoginEmailPasswordController(loginEmailPasswordUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(loginEmailPasswordController)
            .setControllerAdvice(loginEmailPasswordController)
            .build()
    }

    @Test
    fun `should login user`() {
        val email = "user@email.com"
        val password = "password"
        val loginRequestCommand = LoginRequestCommand(email, password)

        val loginResponse = LoginResponse(id = UserId(), email = "email@online.com")
        `when`(loginEmailPasswordUseCase.execute(loginRequestCommand)).thenReturn(loginResponse)

        val response = loginEmailPasswordController.login(loginRequestCommand)

        assertEquals(OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(loginResponse.id.value, response.body!!.id.value)
    }


    @Test
    fun `should handle exceptions user`() {
        val loginError = "Invalid user"

        val response = loginEmailPasswordController.handleLoginException(LoginException(loginError))

        assertEquals(UNAUTHORIZED, response.statusCode)
        assertEquals("Login error: $loginError", response.body)
    }
}