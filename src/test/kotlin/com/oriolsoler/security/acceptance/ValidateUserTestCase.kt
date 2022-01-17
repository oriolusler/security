package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyService
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.Verification
import com.oriolsoler.security.domain.verification.VerificationType
import com.oriolsoler.security.domain.verification.VerificationType.*
import com.oriolsoler.security.infrastucutre.controller.validateuser.ValidateUserCommand
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.*
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class ValidateUserTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var verifyService: VerifyService

    @Autowired
    private lateinit var verifyServiceRepository: VerifyServiceRepository

    @Autowired
    private lateinit var verificationRepositoryForTest: VerificationRepositoryForTest

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
        userRepositoryForTest.clean()
    }

    @Test
    internal fun `should verify verification`() {
        val user = User(email = "email@online.com", password = "extremely_safe_password", locked = true)
        userRepository.save(user)
        assertTrue { user.locked }

        val verification = verifyService.generate(VALIDATE_USER)
        val userVerification = UserVerification(user, verification)
        verifyServiceRepository.save(userVerification)
        assertFalse { userVerification.verification.validated }

        val validateUserCommand = ValidateUserCommand(user.email, verification.verification)
        given()
            .contentType("application/json")
            .body(validateUserCommand)
            .post("/api/auth/validate/user")
            .then()
            .status(ACCEPTED)

        val userPost = userRepository.getBy(user.id)
        assertFalse { userPost.locked }

        val verificationPost = verificationRepositoryForTest.getBy(userVerification)
        assertTrue { verificationPost.verification.validated }
        assertFalse { verificationPost.verification.usable }
    }

    @Test
    internal fun `should handle expired errors`() {
        val user = User(email = "email@online.com", password = "extremely_safe_password", locked = true)
        userRepository.save(user)

        val now = LocalDateTime.now()
        val verification = Verification("865142", now, now.minusHours(1), type = VALIDATE_USER)
        val userVerification = UserVerification(user, verification)
        verifyServiceRepository.save(userVerification)

        val validateUserCommand = ValidateUserCommand(user.email, verification.verification)

        given()
            .contentType("application/json")
            .body(validateUserCommand)
            .post("/api/auth/validate/user")
            .then()
            .status(GONE)
            .body(equalTo("Validate user error: Verification expired"))
    }
}