package com.oriolsoler.security.application.login.firebase

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.infrastucutre.controller.login.firebase.LoginFirebaseRequestCommand
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import com.oriolsoler.security.infrastucutre.controller.login.ResponseUser
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException

class LoginFirebaseUseCase(
    private val userRepository: UserRepository,
    private val firebaseService: FirebaseService,
    private val tokenGenerator: TokenGenerator
) {
    fun execute(loginFirebaseRequestCommand: LoginFirebaseRequestCommand): LoginResponse {
        val firebaseUserVerified = firebaseService.validateToken(loginFirebaseRequestCommand.firebaseToken)
        val user = getUser(firebaseUserVerified.email)
        return LoginResponse(
            tokenGenerator.generate(user.id),
            ResponseUser(id = user.id.value.toString(), email = user.email)
        )
    }

    private fun getUser(email: String): User {
        return try {
            userRepository.getBy(email)
        } catch (e: UserNotFoundException) {
            saveNewUser(email)
        }
    }

    private fun saveNewUser(email: String): User {
        return userRepository.save(User(email = email))
    }
}