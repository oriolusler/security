package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
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
abstract class VerifyVerificationTestCase {

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

        val verification = verifyService.generate()
        val userVerification = UserVerification(user, verification)
        verifyServiceRepository.save(userVerification)
        assertFalse { userVerification.verification.used }

        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification.verification)
        given()
            .contentType("application/json")
            .body(verifyVerificationCommand)
            .post("/api/auth/verify")
            .then()
            .status(ACCEPTED)

        val userPost = userRepository.getBy(user.id)
        assertFalse { userPost.locked }

        val verificationPost = verificationRepositoryForTest.getBy(userVerification)
        assertTrue { verificationPost.verification.used }
    }

    @Test
    internal fun `should handle expired errors`() {
        val user = User(email = "email@online.com", password = "extremely_safe_password", locked = true)
        userRepository.save(user)

        val now = LocalDateTime.now()
        val verification = Verification("687123", now, now.minusHours(1))
        val userVerification = UserVerification(user, verification)
        verifyServiceRepository.save(userVerification)

        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification.verification)

        given()
            .contentType("application/json")
            .body(verifyVerificationCommand)
            .post("/api/auth/verify")
            .then()
            .status(GONE)
            .body(equalTo("Verification error: Expired"))
    }

    @Test
    internal fun `should handle used errors`() {
        val user = User(email = "email@online.com", password = "extremely_safe_password", locked = true)
        userRepository.save(user)

        val verification = Verification("687123", used = true)
        val userVerification = UserVerification(user, verification)
        verifyServiceRepository.save(userVerification)

        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification.verification)

        given()
            .contentType("application/json")
            .body(verifyVerificationCommand)
            .post("/api/auth/verify")
            .then()
            .status(CONFLICT)
            .body(equalTo("Verification error: Used"))
    }
}