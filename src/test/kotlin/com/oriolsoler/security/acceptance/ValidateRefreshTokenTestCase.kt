package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.infrastucutre.controller.login.LoginEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.controller.validaterefreshtoken.ValidateRefreshTokenCommand
import com.oriolsoler.security.infrastucutre.controller.validateuser.ValidateUserCommand
import com.oriolsoler.security.infrastucutre.controller.validateuser.ValidateUserController
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class ValidateRefreshTokenTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @Autowired
    private lateinit var loginEmailPasswordController: LoginEmailPasswordController

    @Autowired
    private lateinit var signupController: SignUpEmailPasswordController

    @Autowired
    private lateinit var validateUserController: ValidateUserController

    @Autowired
    private lateinit var verificationRepositoryForTest: VerificationRepositoryForTest

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
        userRepositoryForTest.clean()
    }

    @Test
    internal fun `should validate refresh token`() {
        val email = "email@online.com"
        val password = "password"

        val signUpRequestCommand = SignUpRequestCommand(email, password)
        signupController.register(signUpRequestCommand)

        val user = userRepository.getBy(email)
        val verification = verificationRepositoryForTest.getNotValidatedBy(user)
        val verifyCommand = ValidateUserCommand(email, verification.verification.verification)
        validateUserController.verify(verifyCommand)

        val loginRequestCommand = LoginRequestCommand(email, password)
        val loginResponse = loginEmailPasswordController.login(loginRequestCommand)

        val refreshToken = loginResponse.body!!.token.refreshToken
        val validateRefreshTokenCommand = ValidateRefreshTokenCommand(refreshToken)

        given()
            .contentType("application/json")
            .body(validateRefreshTokenCommand)
            .post("/api/auth/refresh")
            .then()
            .status(OK)
    }

    @Test
    internal fun `should return unauthorized if error is raised`() {
        val email = "email@online.com"
        val password = "password"

        val signUpRequestCommand = SignUpRequestCommand(email, password)
        signupController.register(signUpRequestCommand)

        val user = userRepository.getBy(email)
        val verification = verificationRepositoryForTest.getNotValidatedBy(user)
        val verifyCommand = ValidateUserCommand(email, verification.verification.verification)
        validateUserController.verify(verifyCommand)

        val loginRequestCommand = LoginRequestCommand(email, password)
        val loginResponse = loginEmailPasswordController.login(loginRequestCommand)

        val refreshToken = loginResponse.body!!.token.refreshToken
        val validateRefreshTokenCommand = ValidateRefreshTokenCommand(refreshToken)

        userRepositoryForTest.lock(user)

        given()
            .contentType("application/json")
            .body(validateRefreshTokenCommand)
            .post("/api/auth/refresh")
            .then()
            .status(UNAUTHORIZED)
    }
}