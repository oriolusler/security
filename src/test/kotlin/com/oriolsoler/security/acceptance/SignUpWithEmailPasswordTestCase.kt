package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class SignUpWithEmailPasswordTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepositoryForTest.clean()
        RestAssuredMockMvc.mockMvc(mvc)
    }

    @Test
    internal fun `should register a new user successfully`() {
        val signUpRequestCommand = SignUpRequestCommand("email@online.com", "password")
        given()
            .contentType("application/json")
            .body(signUpRequestCommand)
            .post("/api/auth/register")
            .then()
            .status(CREATED)
    }

    @Test
    internal fun `should handle error if email is already used`() {
        val signUpRequestCommand = SignUpRequestCommand("email@online.com", "password")
        userRepository.save(User(email = signUpRequestCommand.email, password = signUpRequestCommand.password))

        given()
            .contentType("application/json")
            .body(signUpRequestCommand)
            .post("/api/auth/register")
            .then()
            .status(CONFLICT)
            .body(equalTo("SignUp error: Email already used"))
    }
}