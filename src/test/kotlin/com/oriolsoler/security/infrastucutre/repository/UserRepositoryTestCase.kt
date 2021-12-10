package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserId
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
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    @BeforeEach
    fun setUp() {
        userRepositoryForTest.clean()
    }

    @Test
    fun `save user`() {
        val email = "email@online.com"
        val password = "Encrypted password"
        val user = User(email = email, password = password)

        val result = userRepository.save(user)

        assertNotNull(result)
    }

    @Test
    fun `find user by user id`() {
        val email = "email@hello.com"
        val password = "password"
        val userID = UserId()
        val user = User(id = userID, email = email, password = password)

        userRepository.save(user)

        val userSaved = userRepository.getBy(userID)

        assertNotNull(userSaved)
        assertEquals(email, userSaved.email)
    }

    @Test
    fun `expect error if no user found by email`() {
        val email = "email@hello.com"
        assertFailsWith<Exception> { userRepository.getBy(email) }
    }

    @Test
    fun `expect error if no user found by userId`() {
        val userId = UserId()
        assertFailsWith<Exception> { userRepository.getBy(userId) }
    }
}