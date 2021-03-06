package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.VerificationType
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.ForgotPasswordRequestCommand
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class ForgotPasswordTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var verificationRepositoryForTest: VerificationRepositoryForTest

    @BeforeEach
    fun setUp() {
        userRepositoryForTest.clean()
        RestAssuredMockMvc.mockMvc(mvc)
    }

    @Test
    internal fun `should send email when forgot password`() {
        val email = "email@online.com"
        val user = User(email = email, password = "extremely_safe_password", locked = false)
        userRepository.save(user)

        val forgotPasswordRequestCommand = ForgotPasswordRequestCommand(email)
        given()
            .contentType("application/json")
            .body(forgotPasswordRequestCommand)
            .post("/api/auth/password/forgot")
            .then()
            .status(ACCEPTED)

        val verification = verificationRepositoryForTest.getBy(user)
        assertFalse { verification.verification.validated }
        assertTrue { verification.verification.usable }
        assertEquals(VerificationType.FORGOT_PASSWORD, verification.verification.type)
    }
}