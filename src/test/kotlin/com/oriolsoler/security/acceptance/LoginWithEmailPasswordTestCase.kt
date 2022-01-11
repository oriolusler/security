package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class LoginWithEmailPasswordTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userRepositoryFotTest: UserRepositoryForTest

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
        userRepositoryFotTest.clean()
    }

    @Test
    internal fun `should login successfully`() {
        val email = "email@hello.com"
        val password = "password"
        val roles = listOf(ROLE_USER)
        val user = User(email = email, password = passwordEncoder.encode(password), roles = roles, locked = false)

        userRepository.save(user)

        val signUpRequestCommand = LoginRequestCommand(email, password)
        given()
            .contentType("application/json")
            .body(signUpRequestCommand)
            .post("/api/auth/login")
            .then()
            .status(OK)
            .body("user.id", equalTo(user.id.value.toString()))
            .body("user.email", equalTo(user.email))
            .body("token.accessToken", notNullValue())
            .body("token.refreshToken", notNullValue())
    }

    @Test
    internal fun `should handle exception if user is locked`() {
        val email = "email@hello.com"
        val password = "password"
        val roles = listOf(ROLE_USER)
        val user = User(email = email, password = passwordEncoder.encode(password), roles = roles, locked = true)

        userRepository.save(user)

        val signUpRequestCommand = LoginRequestCommand(email, password)
        given()
            .contentType("application/json")
            .body(signUpRequestCommand)
            .post("/api/auth/login")
            .then()
            .status(FORBIDDEN)
            .body(equalTo("Login error: User locked"))
    }

    @Test
    internal fun `should handle exception if credentials are wrong`() {
        val email = "email@hello.com"
        val password = "password"
        val roles = listOf(ROLE_USER)
        val user = User(email = email, password = passwordEncoder.encode(password), roles = roles, locked = false)

        userRepository.save(user)

        val signUpRequestCommand = LoginRequestCommand(email, "INVALID")
        given()
            .contentType("application/json")
            .body(signUpRequestCommand)
            .post("/api/auth/login")
            .then()
            .status(UNAUTHORIZED)
            .body(equalTo("Login error: Invalid password"))
    }
}