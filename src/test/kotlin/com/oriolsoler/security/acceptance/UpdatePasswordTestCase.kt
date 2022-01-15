package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.Verification
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.UpdatePasswordRequestCommand
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.OK
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class UpdatePasswordTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var verifyRepository: VerifyServiceRepository

    @Autowired
    private lateinit var passwordService: PasswordService

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
        val userSaved = userRepository.save(user)
        assertEquals("extremely_safe_password", userSaved.password)

        val verification = "583152"
        val verificationObject = Verification(verification, used = true)
        val userVerification = UserVerification(user, verificationObject)
        verifyRepository.save(userVerification)

        val newPassword = "extremely_NEW_safe_password"
        val forgotPasswordRequestCommand = UpdatePasswordRequestCommand(email, verification, newPassword)
        given()
            .contentType("application/json")
            .body(forgotPasswordRequestCommand)
            .post("/api/auth/password/update")
            .then()
            .status(OK)

        val userAfter = userRepository.getBy(email)
        assertDoesNotThrow { passwordService.matches(newPassword, userAfter.password) }

        val verificationPost = verificationRepositoryForTest.getBy(userVerification)
        assertTrue { verificationPost.verification.used }
        assertTrue { verificationPost.verification.deleted }
    }
}