package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.login.firebase.FirebaseService
import com.oriolsoler.security.application.login.firebase.LoginFirebaseUseCase
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.services.firebase.FirebaseUser
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.infrastucutre.controller.login.firebase.LoginFirebaseRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private const val FIREBASE_USER_EMAIL = "firebase@email.com"

class LoginFirebaseTestCase {

    @Test
    fun `login with firebase if user doesn't exists`() {
        val firebaseToken = "eyJhbGciOiJSUzI1NiJ9" +
                ".eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbn" +
                "RpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImV4cCI6MTY0NzkwMTExNiwiaWF0IjoxNjQ3ODk3NTE2LCJpc3Mi" +
                "OiJmaXJlYmFzZS1hdXRoLWVtdWxhdG9yQGV4YW1wbGUuY29tIiwic3ViIjoiZmlyZWJhc2UtYXV0aC1lbXVsYXRvckBleG" +
                "FtcGxlLmNvbSIsInVpZCI6IjdsMkVWWVFjbUNXREVMRW03OEUzTHRuVWpZRmoifQ."

        val firebaseUser = FirebaseUser(email = FIREBASE_USER_EMAIL)
        val user = User(email = FIREBASE_USER_EMAIL, locked = false)

        val firebaseService = mock<FirebaseService> {
            on { validateToken(firebaseToken) } doReturn firebaseUser
        }

        val userRepository = mock<UserRepository> {
            on { save(any()) } doReturn user
        }
        given { userRepository.getBy(FIREBASE_USER_EMAIL) } willAnswer { throw UserNotFoundException() }

        val token = Token("extremely_protected_access_jwt", "extremely_protected_refresh_jwt")
        val tokenGenerator = mock<TokenGenerator> {
            on { generate(user.id) } doReturn token
        }

        val loginFirebaseUseCase = LoginFirebaseUseCase(
            userRepository,
            firebaseService,
            tokenGenerator
        )

        val loginFirebaseRequestCommand = LoginFirebaseRequestCommand(firebaseToken)
        val loginResponse = loginFirebaseUseCase.execute(loginFirebaseRequestCommand)

        assertNotNull(loginResponse)
        assertNotNull(loginResponse.token)
        assertTrue { loginResponse.token.accessToken.isNotEmpty() }
        assertTrue { loginResponse.token.refreshToken.isNotEmpty() }
        assertEquals(user.id.value.toString(), loginResponse.user.id)
        assertEquals(FIREBASE_USER_EMAIL, loginResponse.user.email)

        verify(firebaseService).validateToken(firebaseToken)
        verify(userRepository).getBy(FIREBASE_USER_EMAIL)
        verify(userRepository).save(any())
        verify(tokenGenerator).generate(user.id)
    }

    @Test
    fun `login with firebase if user exists`() {
        val firebaseToken = "eyJhbGciOiJSUzI1NiJ9" +
                ".eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbn" +
                "RpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImV4cCI6MTY0NzkwMTExNiwiaWF0IjoxNjQ3ODk3NTE2LCJpc3Mi" +
                "OiJmaXJlYmFzZS1hdXRoLWVtdWxhdG9yQGV4YW1wbGUuY29tIiwic3ViIjoiZmlyZWJhc2UtYXV0aC1lbXVsYXRvckBleG" +
                "FtcGxlLmNvbSIsInVpZCI6IjdsMkVWWVFjbUNXREVMRW03OEUzTHRuVWpZRmoifQ."

        val firebaseUser = FirebaseUser(email = FIREBASE_USER_EMAIL)
        val user = User(email = FIREBASE_USER_EMAIL, locked = false)

        val firebaseService = mock<FirebaseService> {
            on { validateToken(firebaseToken) } doReturn firebaseUser
        }

        val userRepository = mock<UserRepository> {
            on { getBy(FIREBASE_USER_EMAIL) } doReturn user
        }

        val token = Token("extremely_protected_access_jwt", "extremely_protected_refresh_jwt")
        val tokenGenerator = mock<TokenGenerator> {
            on { generate(user.id) } doReturn token
        }

        val loginFirebaseUseCase = LoginFirebaseUseCase(
            userRepository,
            firebaseService,
            tokenGenerator
        )

        val loginFirebaseRequestCommand = LoginFirebaseRequestCommand(firebaseToken)
        val loginResponse = loginFirebaseUseCase.execute(loginFirebaseRequestCommand)

        assertNotNull(loginResponse)
        assertNotNull(loginResponse.token)
        assertTrue { loginResponse.token.accessToken.isNotEmpty() }
        assertTrue { loginResponse.token.refreshToken.isNotEmpty() }
        assertEquals(user.id.value.toString(), loginResponse.user.id)
        assertEquals(FIREBASE_USER_EMAIL, loginResponse.user.email)

        verify(firebaseService).validateToken(firebaseToken)
        verify(userRepository).getBy(FIREBASE_USER_EMAIL)
        verify(tokenGenerator).generate(user.id)
    }

}