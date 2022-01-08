package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.user.UserAlreadyExistsException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.*

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
abstract class UserRepositoryTestCase {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userRepositoryForTest: UserRepositoryForTest

    private val email = "email@online.com"
    private val password = "Encrypted password"
    private val user = User(email = email, password = password)

    @BeforeEach
    fun setUp() {
        userRepositoryForTest.clean()
    }

    @Test
    fun `save user`() {
        val result = userRepository.save(user)
        assertNotNull(result)
    }

    @Test
    fun `find user by user id`() {
        val userID = UserId()
        val user = User(id = userID, email = email, password = password)

        userRepository.save(user)

        val userSaved = userRepository.getBy(userID)

        assertNotNull(userSaved)
        assertEquals(email, userSaved.email)
    }

    @Test
    fun `expect error if no user found by email`() {
        assertFailsWith<Exception> { userRepository.getBy(email) }
    }

    @Test
    fun `expect error if no user found by userId`() {
        val userId = UserId()
        assertFailsWith<Exception> { userRepository.getBy(userId) }
    }

    @Test
    fun `should unlock used`() {
        val user = User(email = email, password = password)
        assertTrue { user.locked }
        userRepository.save(user)

        userRepository.setUnlocked(user)

        val userPost = userRepository.getBy(user.id)
        assertFalse { userPost.locked }
    }

    @Test
    fun `should throw error if user already exists`() {
        val user = User(email = email, password = password)
        userRepository.save(user)

        val e = assertThrows<UserAlreadyExistsException> { userRepository.checkIfUserAlreadyExists(user.email) }
        assertEquals("User already exists", e.message)
    }
}