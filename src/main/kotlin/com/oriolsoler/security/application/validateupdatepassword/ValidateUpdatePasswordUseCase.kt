package com.oriolsoler.security.application.validateupdatepassword

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyService
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.infrastucutre.controller.validateupdatepassword.ValidateUpdatedPasswordCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException

class ValidateUpdatePasswordUseCase(
    val verifyService: VerifyService,
    val verifyServiceRepository: VerifyServiceRepository,
    val userRepository: UserRepository
) {
    fun execute(validateUserCommand: ValidateUpdatedPasswordCommand) {
        val user = getUserByEmail(validateUserCommand.email)
        val userVerification = getUserVerification(user, validateUserCommand.verification)
        checkIfTokenIsValid(userVerification)
        updateVerificationStatus(userVerification)
    }

    private fun updateVerificationStatus(userVerification: UserVerification) {
        verifyServiceRepository.setToValidated(userVerification)
    }

    private fun getUserByEmail(email: String): User = try {
        userRepository.getBy(email)
    } catch (e: UserNotFoundException) {
        throw ValidateUpdatePasswordException(e.message, e)
    }

    private fun getUserVerification(user: User, verification: String) = try {
        verifyServiceRepository.getBy(user, verification)
    } catch (e: VerificationNotFoundException) {
        throw ValidateUpdatePasswordException(e.message, e)
    }

    private fun checkIfTokenIsValid(userVerification: UserVerification) = try {
        verifyService.checkIfAlreadyValidated(userVerification.verification)
        verifyService.checkIfExpired(userVerification.verification)
    } catch (e: VerificationExpiredException) {
        throw ValidateUpdatePasswordException(e.message, e)
    } catch (e: VerificationAlreadyVerifiedException) {
        throw ValidateUpdatePasswordException(e.message, e)
    }
}