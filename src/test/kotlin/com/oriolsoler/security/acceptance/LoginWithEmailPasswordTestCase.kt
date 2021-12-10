package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserRole
import com.oriolsoler.security.domain.user.UserRole.*
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.OK
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class LoginWithEmailPasswordTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var signUpUserRepository: SignUpUserRepository

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
        val user = User(email = email, password = passwordEncoder.encode(password), roles = roles)

        signUpUserRepository.save(user)

        val signUpRequestCommand = LoginRequestCommand(email, password)
        given()
            .contentType("application/json")
            .body(signUpRequestCommand)
            .post("/api/auth/login")
            .then()
            .status(OK)
    }
}