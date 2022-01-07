package com.oriolsoler.security.application.accessverification

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.services.exceptions.TokenValidationException
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserLockedException
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
        } catch (e: TokenValidationException) {
            throw AccessVerificationException(e.message, e)
        }
    }

    private fun isValidUser(user: User) {
        try {
            user.isValid()
        } catch (e: UserLockedException) {
            throw AccessVerificationException(e.message, e)
        }
    }
}