package com.oriolsoler.security.infrastucutre.security

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UserDetails
import kotlin.test.*

class UserServiceTest {

    @Test
    fun `should get UserDetails from user repository`() {
        val user = User()
        val userRepository = mock<UserRepository> {
            on { getBy(user.id) } doReturn user
        }
        val userService = UserService(userRepository)

        val response = userService.loadUserByUsername(user.id.value.toString())

        assertNotNull(response)
        assertIs<UserDetails>(response)
        assertIs<UserDetailsImpl>(response)
        assertEquals(user.password, response.getPassword())
        assertEquals("", response.getUsername())
        assertFalse {
            response.isAccountNonExpired()
            response.isCredentialsNonExpired()
        }
        assertTrue {
            response.isAccountNonLocked()
            response.isEnabled()
        }
        assertEquals(user.roles.size, response.getAuthorities().size)
    }
}