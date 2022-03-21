package com.oriolsoler.security.domain.services.firebase

import com.google.firebase.auth.FirebaseAuth
import com.oriolsoler.security.application.login.firebase.FirebaseService

class FirebaseServiceImpl(private val firebaseAuthInstance: FirebaseAuth) : FirebaseService {
    override fun validateToken(firebaseToken: String): FirebaseUser {
        val userVerified = firebaseAuthInstance.verifyIdToken(firebaseToken)
        return FirebaseUser(email = userVerified.email)
    }
}