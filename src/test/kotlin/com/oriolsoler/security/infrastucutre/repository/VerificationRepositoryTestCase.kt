package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.application.signup.VerifyService
import com.oriolsoler.security.application.signup.VerifyServiceRepository
import com.oriolsoler.security.domain.User
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

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
abstract class VerificationRepositoryTestCase {
    @Autowired
    private lateinit var signUpUserRepository: SignUpUserRepository

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
        signUpUserRepository.save(user)
    }

    @AfterEach
    fun cleanUp() {
        userRepositoryForTest.clean()
        verificationRepositoryForTest.clean()
    }

    @Test
    fun `save verification`() {
        val verificationCode = verifyService.generate()
        val verification = Verification(user = user, verification = verificationCode)

        val result = verifyServiceRepository.save(verification)

        assertNotNull(result)
    }

    @Test
    fun `get verification by user`() {
        val verificationCode = verifyService.generate()
        val verification = Verification(user = user, verification = verificationCode)
        verifyServiceRepository.save(verification)

        val result = verifyServiceRepository.getUnusedBy(user)

        assertNotNull(result)
        assertEquals(user, result.user)
    }

    @Test
    fun `expect error if no validation found`() {
        assertFailsWith<Exception> { verifyServiceRepository.getUnusedBy(user) }
    }
}