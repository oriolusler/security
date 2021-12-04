package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
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
abstract class UserRepositoryTestCase {
    @Autowired
    private lateinit var signUpUserRepository: SignUpUserRepository

    @Autowired
    private lateinit var loginUserRepository: LoginUserRepository

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @BeforeEach
    fun setUp() {
        userRepositoryForTest.clean()
    }

    @Test
    fun `create user with default role`() {
        val email = "email@hello.com"
        val password = "password"

        signUpUserRepository.save(email, password)

        val userSaved = loginUserRepository.getBy(email)

        assertNotNull(userSaved)
        assertEquals(email, userSaved.email)
        assertEquals(1, userSaved.roles.size)
        assertEquals(ROLE_USER, userSaved.roles[0])
    }

    @Test
    fun `find user by user id`() {
        val email = "email@hello.com"
        val password = "password"
        val userID = UserId()

        userRepositoryForTest.save(userID, email, password)

        val userSaved = loginUserRepository.getBy(userID)

        assertNotNull(userSaved)
        assertEquals(email, userSaved.email)
    }

    @Test
    fun `expect error if no user found by email`() {
        val email = "email@hello.com"
        assertFailsWith<Exception> { loginUserRepository.getBy(email) }
    }

    @Test
    fun `expect error if no user found by userId`() {
        val userId = UserId()
        assertFailsWith<Exception> { loginUserRepository.getBy(userId) }
    }
}