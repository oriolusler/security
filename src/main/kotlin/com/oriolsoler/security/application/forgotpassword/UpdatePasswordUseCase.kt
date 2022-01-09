package com.oriolsoler.security.application.forgotpassword

import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.VerificationNotVerifiedException
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.UpdatePasswordRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException

class UpdatePasswordUseCase(
    private val userRepository: UserRepository,
    private val verifyService: VerifyService,
    private val verifyServiceRepository: VerifyServiceRepository,
    private val passwordService: PasswordService
) {
    fun execute(updatePasswordCommand: UpdatePasswordRequestCommand) {
        val currentUser = getUserByEmail(updatePasswordCommand.email)
        val userVerification = getUserVerification(currentUser, updatePasswordCommand)
        checkIfUserVerificationHasNotBeenUsed(userVerification)
        userRepository.updatePassword(currentUser, passwordService.encode(updatePasswordCommand.newPassword))
    }

    private fun checkIfUserVerificationHasNotBeenUsed(userVerification: UserVerification) {
        try {
            verifyService.validateIfNotUsed(userVerification.verification)
        } catch (e: VerificationNotVerifiedException) {
            throw UpdatePasswordException(e.message, e)
        }
    }

    private fun getUserVerification(
        currentUser: User,
        updatePasswordCommand: UpdatePasswordRequestCommand
    ): UserVerification {
        try {
            return verifyServiceRepository.getBy(currentUser, updatePasswordCommand.verification)
        } catch (e: VerificationNotFoundException) {
            throw UpdatePasswordException(e.message, e)
        }
    }

    private fun getUserByEmail(email: String): User {
        return try {
            userRepository.getBy(email)
        } catch (e: UserNotFoundException) {
            throw UpdatePasswordException(e.message, e)
        }
    }
}