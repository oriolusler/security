package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.verification.VerificationException
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import com.oriolsoler.security.infrastucutre.repository.verification.VerifyRepositoryError

class VerifyVerificationUseCase(
    val verifyService: VerifyService,
    val verifyServiceRepository: VerifyServiceRepository,
    val userRepository: UserRepository
) {
    fun execute(verifyVerificationCommand: VerifyVerificationCommand) {
        val user = userRepository.getBy(verifyVerificationCommand.email)
        val userVerification = getUserVerification(user, verifyVerificationCommand)
        if (isValid(userVerification)) {
            verifyServiceRepository.setToUsed(userVerification)
            userRepository.setUnlocked(user)
        }
    }

    private fun getUserVerification(user: User, verifyVerificationCommand: VerifyVerificationCommand) = try {
        verifyServiceRepository.getUnusedBy(user, verifyVerificationCommand.verification)
    } catch (e: VerifyRepositoryError) {
        throw VerifyException(e.message, e)
    }


    private fun isValid(userVerification: UserVerification) = try {
        verifyService.isValid(userVerification)
    } catch (e: VerificationException) {
        throw VerifyException(e.message, e)
    }
}