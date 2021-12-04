package com.oriolsoler.security.infrastucutre.security.filter

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.security.UserDetailsImpl
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthTokenFilterTest {
    @Test
    fun `register user in spring security`() {
        //Reset os security context
        SecurityContextHolder.getContext().authentication = null

        val token = "8a9b5c3d2e9f0g8h5i7j"
        val user = User()
        val jwtTokenService = mock<TokenGenerator> {
            on { getUserIdFromToken(token) } doReturn user.id.value.toString()
            on { isValid(token) } doReturn true
        }
        val userDetailsService = mock<UserDetailsService> {
            on { loadUserByUsername(user.id.value.toString()) } doReturn UserDetailsImpl(user)
        }
        val request = mock<HttpServletRequest> {
            on { getHeader("Authorization") } doReturn "Bearer $token"
        }
        val response = mock<HttpServletResponse> {}
        val filterChain = mock<FilterChain> {}

        val autTokenFilter = AuthTokenFilter(jwtTokenService, userDetailsService)

        autTokenFilter.doFilter(request, response, filterChain)

        val authenticationFromSecurityContext: UserDetailsImpl =
            SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl

        assertEquals(user.id, authenticationFromSecurityContext.user.id)
        verify(jwtTokenService, times(1)).getUserIdFromToken(token)
        verify(userDetailsService, times(1)).loadUserByUsername(user.id.value.toString())
    }

    @Test
    fun `no register in spring context if token is not valid`() {
        //Reset os security context
        SecurityContextHolder.getContext().authentication = null

        val token = "8a9b5c3d2e9f0g8h5i7j"
        val user = User()
        val jwtTokenService = mock<TokenGenerator> {
            on { getUserIdFromToken(token) } doReturn user.id.value.toString()
            on { isValid(token) } doReturn false
        }
        val userDetailsService = mock<UserDetailsService> {
            on { loadUserByUsername(user.id.value.toString()) } doReturn UserDetailsImpl(user)
        }
        val request = mock<HttpServletRequest> {
            on { getHeader("Authorization") } doReturn "Bearer $token"
        }
        val response = mock<HttpServletResponse> {}
        val filterChain = mock<FilterChain> {}

        val autTokenFilter = AuthTokenFilter(jwtTokenService, userDetailsService)

        autTokenFilter.doFilter(request, response, filterChain)

        assertFailsWith<NullPointerException> {
            SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        }
        verify(jwtTokenService, times(1)).isValid(token)
        verify(userDetailsService, times(0)).loadUserByUsername(user.id.value.toString())
    }

    @Test
    fun `no register in spring context if token is empty`() {
        //Reset os security context
        SecurityContextHolder.getContext().authentication = null

        val token = ""
        val user = User()
        val jwtTokenService = mock<TokenGenerator> { }
        val userDetailsService = mock<UserDetailsService> {}
        val request = mock<HttpServletRequest> {
            on { getHeader("Authorization") } doReturn token
        }

        val response = mock<HttpServletResponse> {}
        val filterChain = mock<FilterChain> {}

        val autTokenFilter = AuthTokenFilter(jwtTokenService, userDetailsService)

        autTokenFilter.doFilter(request, response, filterChain)

        assertFailsWith<NullPointerException> {
            SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        }
        verify(jwtTokenService, times(0)).getUserIdFromToken(token)
        verify(userDetailsService, times(0)).loadUserByUsername(user.id.value.toString())
    }

    @Test
    fun `no register in spring context if token does not start properly`() {
        //Reset os security context
        SecurityContextHolder.getContext().authentication = null

        val token = "INVALID TOKEN"
        val user = User()
        val jwtTokenService = mock<TokenGenerator> { }
        val userDetailsService = mock<UserDetailsService> {}
        val request = mock<HttpServletRequest> {
            on { getHeader("Authorization") } doReturn token
        }

        val response = mock<HttpServletResponse> {}
        val filterChain = mock<FilterChain> {}

        val autTokenFilter = AuthTokenFilter(jwtTokenService, userDetailsService)

        autTokenFilter.doFilter(request, response, filterChain)

        assertFailsWith<NullPointerException> {
            SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl
        }
        verify(jwtTokenService, times(0)).getUserIdFromToken(token)
        verify(userDetailsService, times(0)).loadUserByUsername(user.id.value.toString())
    }
}