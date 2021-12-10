package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.signup.VerifyService
import com.oriolsoler.security.application.signup.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
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

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
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
}