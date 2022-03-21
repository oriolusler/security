package com.oriolsoler.security.controller.login

import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.login.userpassword.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.LoginException
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.services.exceptions.InvalidPasswordException
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserLockedException
import com.oriolsoler.security.infrastucutre.controller.login.emailpassword.LoginEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.login.emailpassword.LoginEmailPasswordRequestCommand
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import com.oriolsoler.security.infrastucutre.controller.login.ResponseUser
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus.*
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
        val loginEmailPasswordRequestCommand = LoginEmailPasswordRequestCommand(email, password)

        val loginResponseExpected = LoginResponse(
            token = Token("", ""),
            user = ResponseUser(UserId().value.toString(), "email@online.com")
        )
        `when`(loginEmailPasswordUseCase.execute(loginEmailPasswordRequestCommand)).thenReturn(loginResponseExpected)

        val response = loginEmailPasswordController.login(loginEmailPasswordRequestCommand)

        assertEquals(OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(loginResponseExpected.user.id, response.body!!.user.id)
    }

    @Test
    fun `should handle users locked`() {
        val userLockedException = UserLockedException()
        val loginException = LoginException(userLockedException.message, userLockedException)
        val response = loginEmailPasswordController.handleLoginException(loginException)

        assertEquals(FORBIDDEN, response.statusCode)
        assertEquals(loginException.message, response.body)
    }

    @Test
    fun `should handle bad password`() {
        val invalidPasswordException = InvalidPasswordException()
        val loginException = LoginException(invalidPasswordException.message, invalidPasswordException)
        val response = loginEmailPasswordController.handleLoginException(loginException)

        assertEquals(UNAUTHORIZED, response.statusCode)
        assertEquals(loginException.message, response.body)
    }

    @Test
    fun `should handle user not found`() {
        val userNotFoundException = UserNotFoundException()
        val loginException = LoginException(userNotFoundException.message, userNotFoundException)
        val response = loginEmailPasswordController.handleLoginException(loginException)

        assertEquals(UNAUTHORIZED, response.statusCode)
        assertEquals(loginException.message, response.body)
    }
}