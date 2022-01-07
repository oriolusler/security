package com.oriolsoler.security.controller.validaterefreshtoken

import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenException
import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenUseCase
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.services.exceptions.TokenValidationException
import com.oriolsoler.security.infrastucutre.controller.validaterefreshtoken.ValidateRefreshTokenCommand
import com.oriolsoler.security.infrastucutre.controller.validaterefreshtoken.ValidateRefreshTokenController
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
class ValidateRefreshTokenControllerTest {
    private lateinit var mockMvc: MockMvc
    private val validateRefreshTokenUseCase = mock<ValidateRefreshTokenUseCase> {}
    private val verificationController = ValidateRefreshTokenController(validateRefreshTokenUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(verificationController)
            .setControllerAdvice(verificationController)
            .build()
    }

    @Test
    fun `should validate refresh token`() {
        val refreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9" +
                ".eyJzdWIiOiJiNmMyNDU4ZC02NTJjLTQwNjEtOTVlNy00ZjQ3YjBkZTJiOTQifQ" +
                ".BYGZ26WmXqSi5GWdKCGXzIjebofEZcIfSOQYqYuXkbqlFnHgZGXQrTRyVxi9BRypibNgrGKnspMM-eIayx1Rmw"

        val validateRefreshTokenCommand = ValidateRefreshTokenCommand(refreshToken)
        val newToken = Token("new_access_token", "new_refresh_token")
        `when`(validateRefreshTokenUseCase.execute(validateRefreshTokenCommand)).thenReturn(newToken)

        val response = verificationController.validate(validateRefreshTokenCommand)

        assertEquals(OK, response.statusCode)
        assertNotNull(response.body)
        assertNotNull(response.body!!.accessToken)
        assertNotNull(response.body!!.refreshToken)
    }

    @Test
    fun `should unauthorized request if refresh token validation fails`() {
        val verificationError = TokenValidationException("Expired")
        val validateRefreshToken = ValidateRefreshTokenException(verificationError.message, verificationError)

        val response = verificationController.handleValidateRefreshTokenException(validateRefreshToken)

        assertEquals(UNAUTHORIZED, response.statusCode)
        assertEquals(validateRefreshToken.message, response.body)
    }
}