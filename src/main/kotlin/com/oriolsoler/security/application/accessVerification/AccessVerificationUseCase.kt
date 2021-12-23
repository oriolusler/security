package com.oriolsoler.security.application.accessVerification

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserException
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.infrastucutre.controller.accessVerification.AccessVerificationCommand

class AccessVerificationUseCase(
    private val tokenVerification: TokenVerification,
    private val userRepository: UserRepository
) {
    fun execute(accessVerificationCommand: AccessVerificationCommand): Boolean {
        val currentUserUUID = tokenVerification.validate(accessVerificationCommand.token)
        val user = userRepository.getBy(UserId(currentUserUUID))
        isValidUser(user)
        return true
    }

    private fun isValidUser(user: User) {
        try {
            user.isValid()
        } catch (e: UserException) {
            throw AccessVerificationException(e.message)
        }
    }
}