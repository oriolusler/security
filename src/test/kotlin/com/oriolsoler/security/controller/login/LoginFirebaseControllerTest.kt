package com.oriolsoler.security.controller.login

import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.login.firebase.LoginFirebaseUseCase
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import com.oriolsoler.security.infrastucutre.controller.login.ResponseUser
import com.oriolsoler.security.infrastucutre.controller.login.firebase.LoginFirebaseController
import com.oriolsoler.security.infrastucutre.controller.login.firebase.LoginFirebaseRequestCommand
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
class LoginFirebaseControllerTest {
    private lateinit var mockMvc: MockMvc
    private val loginFirebaseUseCase = mock<LoginFirebaseUseCase> {}
    private val loginFirebaseController = LoginFirebaseController(loginFirebaseUseCase)

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(loginFirebaseController)
            .setControllerAdvice(loginFirebaseController)
            .build()
    }

    @Test
    fun `should login user`() {
        val firebaseToken = "eyJhbGciOiJSUzI1NiJ9" +
                ".eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbn" +
                "RpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImV4cCI6MTY0NzkwMTExNiwiaWF0IjoxNjQ3ODk3NTE2LCJpc3Mi" +
                "OiJmaXJlYmFzZS1hdXRoLWVtdWxhdG9yQGV4YW1wbGUuY29tIiwic3ViIjoiZmlyZWJhc2UtYXV0aC1lbXVsYXRvckBleG" +
                "FtcGxlLmNvbSIsInVpZCI6IjdsMkVWWVFjbUNXREVMRW03OEUzTHRuVWpZRmoifQ."
        val loginFirebaseRequestCommand = LoginFirebaseRequestCommand(firebaseToken)

        val loginResponseExpected = LoginResponse(
            token = Token("", ""),
            user = ResponseUser(UserId().value.toString(), "firebase@email.com")
        )
        `when`(loginFirebaseUseCase.execute(loginFirebaseRequestCommand)).thenReturn(loginResponseExpected)

        val response = loginFirebaseController.login(loginFirebaseRequestCommand)

        assertEquals(OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(loginResponseExpected.user.id, response.body!!.user.id)
    }
}