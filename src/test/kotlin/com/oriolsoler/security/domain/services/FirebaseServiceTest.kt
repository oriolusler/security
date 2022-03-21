package com.oriolsoler.security.domain.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.domain.services.firebase.FirebaseServiceImpl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private const val FIREBASE_USER_EMAIL = "firebase@email.com"

class FirebaseServiceTest {
    @Test
    fun `should validate token`() {
        val firebaseToken = "eyJhbGciOiJSUzI1NiJ9" +
                ".eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbn" +
                "RpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImV4cCI6MTY0NzkwMTExNiwiaWF0IjoxNjQ3ODk3NTE2LCJpc3Mi" +
                "OiJmaXJlYmFzZS1hdXRoLWVtdWxhdG9yQGV4YW1wbGUuY29tIiwic3ViIjoiZmlyZWJhc2UtYXV0aC1lbXVsYXRvckBleG" +
                "FtcGxlLmNvbSIsInVpZCI6IjdsMkVWWVFjbUNXREVMRW03OEUzTHRuVWpZRmoifQ."

        val firebaseUser = mock<FirebaseToken> {
            on { email } doReturn "firebase@email.com"
        }

        val firebaseAuthInstance = mock<FirebaseAuth> {
            on { verifyIdToken(firebaseToken) } doReturn firebaseUser
        }

        val firebaseService = FirebaseServiceImpl(firebaseAuthInstance)

        val userValidated = firebaseService.validateToken(firebaseToken)

        assertEquals(FIREBASE_USER_EMAIL, userValidated.email)
    }
}