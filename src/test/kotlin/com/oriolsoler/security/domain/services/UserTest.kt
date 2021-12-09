package com.oriolsoler.security.domain.services

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserId
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserTest {
    @Test
    fun `user with same id should be de same`() {
        val usrId = UserId()
        val user1 = User(id = usrId)
        val user2 = User(id = usrId)

        assertTrue { user1.equals(user2) }
    }

    @Test
    fun `user with different ids should not be equals`() {
        val user1 = User(id = UserId())
        val user2 = User(id = UserId())

        assertFalse { user1.equals(user2) }
    }

    @Test
    fun `user must have unique hash`() {
        val user = User(id = UserId())
        assertNotNull(user.hashCode())
    }
}