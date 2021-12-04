package com.oriolsoler.security.infrastucutre.security

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserId
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.springframework.security.core.userdetails.UserDetails
import kotlin.test.*

class UserServiceTest {

    @Test
    fun `should get UserDetails from user repository`() {
        val user = User()
        val loginUserRepository = mock<LoginUserRepository> {
            on { getBy(user.id) } doReturn user
        }
        val userService = UserService(loginUserRepository)

        val response = userService.loadUserByUsername(user.id.value.toString())

        assertNotNull(response)
        assertIs<UserDetails>(response)
        assertIs<UserDetailsImpl>(response)
        assertEquals(user.password, response.getPassword())
        assertEquals(user.name, response.getUsername())
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