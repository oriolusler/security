package com.oriolsoler.security.application.login.firebase

import com.oriolsoler.security.domain.services.firebase.FirebaseUser

interface FirebaseService {
    fun validateToken(firebaseToken: String): FirebaseUser
}