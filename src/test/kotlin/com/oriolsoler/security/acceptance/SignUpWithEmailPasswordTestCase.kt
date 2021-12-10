package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.CREATED
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class SignUpWithEmailPasswordTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
    }

    @Test
    internal fun `should register a new user successfully`() {
        val signUpRequestCommand = SignUpRequestCommand("oriol.soler@hotmail.com", "password")
        given()
            .contentType("application/json")
            .body(signUpRequestCommand)
            .post("/api/auth/register")
            .then()
            .status(CREATED)
    }
}