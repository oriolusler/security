package com.oriolsoler.security.application.validateuser

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyService
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.infrastucutre.controller.validateuser.ValidateUserCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException

class ValidateUserUseCase(
    val verifyService: VerifyService,
    val verifyServiceRepository: VerifyServiceRepository,
    val userRepository: UserRepository
) {
    fun execute(validateUserCommand: ValidateUserCommand) {
        val user = getUserByEmail(validateUserCommand.email)
        val userVerification = getUserVerification(user, validateUserCommand.verification)
        checkIfValidationIsValid(userVerification)
        updateVerificationStatus(userVerification)
        userRepository.setUnlocked(user)
    }

    private fun updateVerificationStatus(userVerification: UserVerification) {
        verifyServiceRepository.setToValidated(userVerification)
        verifyServiceRepository.setToUnusable(userVerification)
    }

    private fun getUserByEmail(email: String): User {
        return try {
            userRepository.getBy(email)
        } catch (e: UserNotFoundException) {
            throw ValidateUserException(e.message, e)
        }
    }

    private fun getUserVerification(user: User, verification: String) = try {
        verifyServiceRepository.getBy(user, verification)
    } catch (e: VerificationNotFoundException) {
        throw ValidateUserException(e.message, e)
    }

    private fun checkIfValidationIsValid(userVerification: UserVerification) = try {
        verifyService.checkIfAlreadyValidated(userVerification.verification)
        verifyService.checkIfExpired(userVerification.verification)
        verifyService.checkIfUsable(userVerification.verification)
    } catch (e: VerificationExpiredException) {
        throw ValidateUserException(e.message, e)
    } catch (e: VerificationAlreadyVerifiedException) {
        throw ValidateUserException(e.message, e)
    } catch (e: VerificationNotUsableException) {
        throw ValidateUserException(e.message, e)
    }
}