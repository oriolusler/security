package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyService
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.VerificationType.*
import com.oriolsoler.security.infrastucutre.controller.resenduservalidation.ResendUserValidationCommand
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.*
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class ResendUserVerificationMailTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var verificationRepositoryForTest: VerificationRepositoryForTest

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
        userRepositoryForTest.clean()
        verificationRepositoryForTest.clean()
    }

    @Test
    internal fun `should create verification`() {
        val user = User(email = "email@online.com", password = "extremely_safe_password", locked = true)
        userRepository.save(user)

        val resendUserValidationCommand = ResendUserValidationCommand(user.email)
        given()
            .contentType("application/json")
            .body(resendUserValidationCommand)
            .post("/api/auth/validate/validate/user/resend")
            .then()
            .status(ACCEPTED)

        val verification = verificationRepositoryForTest.getBy(user)
        assertFalse { verification.verification.validated }
        assertFalse { verification.verification.usable }
        assertEquals(VALIDATE_USER, verification.verification.type)
    }

}