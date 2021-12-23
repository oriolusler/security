package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
abstract class VerificationRepositoryTestCase {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var verifyServiceRepository: VerifyServiceRepository

    @Autowired
    private lateinit var verifyService: VerifyService

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @Autowired
    private lateinit var verificationRepositoryForTest: VerificationRepositoryForTest

    val user = User(email = "email@online.com", password = "Encrypted password")

    @BeforeEach
    fun setUp() {
        userRepository.save(user)
    }

    @AfterEach
    fun cleanUp() {
        userRepositoryForTest.clean()
        verificationRepositoryForTest.clean()
    }

    @Test
    fun `save verification`() {
        val verification = verifyService.generate()
        val userVerification = UserVerification(user, verification)

        val result = verifyServiceRepository.save(userVerification)

        assertNotNull(result)
    }

    @Test
    fun `get verification by user`() {
        val verification = verifyService.generate()
        val userVerification = UserVerification(user, verification)

        verifyServiceRepository.save(userVerification)

        val result = verifyServiceRepository.getUnusedBy(user)

        assertNotNull(result)
        assertEquals(user, result.user)
    }

    @Test
    fun `expect error if no validation found`() {
        assertFailsWith<Exception> { verifyServiceRepository.getUnusedBy(user) }
    }

    @Test
    fun `should update verification if used`() {
        val verification = Verification(verification = "455123", used = false)
        val userVerification = UserVerification(user, verification)
        verifyServiceRepository.save(userVerification)

        verifyServiceRepository.setToUsed(userVerification)

        val verificationPost = verificationRepositoryForTest.getBy(userVerification)
        assertTrue { verificationPost.verification.used }
    }
}