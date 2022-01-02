package com.oriolsoler.security.application.accessverification

import com.auth0.jwt.exceptions.JWTVerificationException
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserException
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.infrastucutre.controller.accessverification.AccessVerificationCommand

class AccessVerificationUseCase(
    private val tokenVerification: TokenVerification,
    private val userRepository: UserRepository
) {
    fun execute(accessVerificationCommand: AccessVerificationCommand): Boolean {
        val currentUserUUID = isValidVerification(accessVerificationCommand.accessToken)
        val user = userRepository.getBy(currentUserUUID)
        isValidUser(user)
        return true
    }

    private fun isValidVerification(accessToken: String): UserId {
        try {
            return UserId(tokenVerification.validateAccessToken(accessToken))
        } catch (e: JWTVerificationException) {
            throw AccessVerificationException(e.message)
        }
    }

    private fun isValidUser(user: User) {
        try {
            user.isValid()
        } catch (e: UserException) {
            throw AccessVerificationException(e.message)
        }
    }
}