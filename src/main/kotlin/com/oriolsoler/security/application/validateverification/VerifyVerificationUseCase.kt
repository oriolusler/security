package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.signup.VerifyService
import com.oriolsoler.security.application.signup.VerifyServiceRepository
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.verification.VerificationException
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand

class VerifyVerificationUseCase(
    val verifyService: VerifyService,
    val verifyServiceRepository: VerifyServiceRepository,
    val userRepository: UserRepository
) {
    fun execute(verifyVerificationCommand: VerifyVerificationCommand) {
        val user = userRepository.getBy(verifyVerificationCommand.email)
        val userVerification = verifyServiceRepository.getUnusedBy(user, verifyVerificationCommand.verification)
        if (isValid(userVerification)) {
            verifyServiceRepository.setToUsed(userVerification)
            userRepository.setUnlocked(user)
        }
    }

    fun isValid(userVerification: UserVerification): Boolean {
        try {
            return verifyService.isValid(userVerification)
        } catch (e: VerificationException) {
            throw VerifyException(e.message, e)
        }
    }
}