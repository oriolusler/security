package com.oriolsoler.security.application.updatepassword

import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyService
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.VerificationNotUsableException
import com.oriolsoler.security.domain.verification.VerificationNotVerifiedException
import com.oriolsoler.security.infrastucutre.controller.updatepassword.UpdatePasswordRequestCommand
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
        checkIfValidVerification(userVerification)
        updateVerificationStatus(userVerification)
        userRepository.updatePassword(currentUser, passwordService.encode(updatePasswordCommand.newPassword))
    }

    private fun updateVerificationStatus(userVerification: UserVerification) {
        verifyServiceRepository.setToUnusable(userVerification)
    }

    private fun checkIfValidVerification(userVerification: UserVerification) {
        try {
            verifyService.checkIfNotValidated(userVerification.verification)
            verifyService.checkIfUsable(userVerification.verification)
        } catch (e: VerificationNotVerifiedException) {
            throw UpdatePasswordException(e.message, e)
        } catch (e: VerificationNotUsableException) {
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