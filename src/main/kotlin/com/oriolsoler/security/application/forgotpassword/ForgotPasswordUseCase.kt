package com.oriolsoler.security.application.forgotpassword

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.email.ForgotPasswordMailInformation
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.ForgotPasswordRequestCommand

class ForgotPasswordUseCase(
    private val verifyService: VerifyService,
    private val emailService: MailService,
    private val verifyServiceRepository: VerifyServiceRepository,
    private val userRepository: UserRepository,
    private val mailFrom: String
) {
    fun execute(forgotPasswordRequestCommand: ForgotPasswordRequestCommand) {
        val currentUser = userRepository.getBy(forgotPasswordRequestCommand.email)
        val userVerification = UserVerification(currentUser, verifyService.generate())
        verifyServiceRepository.save(userVerification)
        sendMail(userVerification)
    }

    private fun sendMail(userVerification: UserVerification) {
        val forgotPasswordMailInformation = ForgotPasswordMailInformation(
            from = mailFrom,
            to = userVerification.user.email,
            validation = userVerification.verification.verification
        )
        emailService.send(forgotPasswordMailInformation)
    }
}