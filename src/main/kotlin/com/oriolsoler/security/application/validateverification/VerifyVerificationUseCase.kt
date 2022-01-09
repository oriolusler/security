package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException

class VerifyVerificationUseCase(
    val verifyService: VerifyService,
    val verifyServiceRepository: VerifyServiceRepository,
    val userRepository: UserRepository
) {
    fun execute(verifyVerificationCommand: VerifyVerificationCommand) {
        val user = getUserByEmail(verifyVerificationCommand.email)
        val userVerification = getUserVerification(user, verifyVerificationCommand)
        checkIfTokenIsValid(userVerification)
        updateVerificationStatus(userVerification)
        userRepository.setUnlocked(user)
    }

    private fun updateVerificationStatus(userVerification: UserVerification) {
        verifyServiceRepository.setToUsed(userVerification)
    }

    private fun getUserByEmail(email: String): User {
        return try {
            userRepository.getBy(email)
        } catch (e: UserNotFoundException) {
            throw VerifyException(e.message, e)
        }
    }

    private fun getUserVerification(user: User, verifyVerificationCommand: VerifyVerificationCommand) = try {
        verifyServiceRepository.getBy(user, verifyVerificationCommand.verification)
    } catch (e: VerificationNotFoundException) {
        throw VerifyException(e.message, e)
    }

    private fun checkIfTokenIsValid(userVerification: UserVerification) = try {
        verifyService.validateIfUsed(userVerification.verification)
        verifyService.validateIfExpired(userVerification.verification)
    } catch (e: VerificationExpiredException) {
        throw VerifyException(e.message, e)
    } catch (e: VerificationUsedException) {
        throw VerifyException(e.message, e)
    }
}