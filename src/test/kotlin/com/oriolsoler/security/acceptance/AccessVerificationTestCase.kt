package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.infrastucutre.controller.accessverification.AccessVerificationCommand
import com.oriolsoler.security.infrastucutre.controller.accessverification.AccessVerificationController
import com.oriolsoler.security.infrastucutre.controller.login.LoginEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpEmailPasswordController
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationController
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import io.restassured.http.Header
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.GONE
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class AccessVerificationTestCase {

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
    private lateinit var verifyVerificationController: VerifyVerificationController

    @Autowired
    private lateinit var verificationRepository: VerifyServiceRepository

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
        userRepositoryForTest.clean()
    }

    @Test
    internal fun `should verify access`() {
        val email = "email@online.com"
        val password = "password"

        val signUpRequestCommand = SignUpRequestCommand(email, password)
        signupController.register(signUpRequestCommand)

        val user = userRepository.getBy(email)
        val verification = verificationRepository.getUnusedBy(user)
        val verifyCommand = VerifyVerificationCommand(email, verification.verification.verification)
        verifyVerificationController.verify(verifyCommand)

        val loginRequestCommand = LoginRequestCommand(email, password)
        val loginResponse = loginEmailPasswordController.login(loginRequestCommand)

        val accessToken = loginResponse.body!!.token

        given()
            .contentType("application/json")
            .header(Header("Authorization", accessToken))
            .get("/api/auth/validate")
            .then()
            .status(ACCEPTED)
    }
}