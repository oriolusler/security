package com.oriolsoler.security.application.resenduserverification

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyService
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.domain.email.ValidateEmailMailInformation
import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.VerificationType
import com.oriolsoler.security.infrastucutre.controller.resenduserverification.ResendUserVerificationCommand

class ResendUserVerificationUseCase(
    private val userRepository: UserRepository,
    private val verifyService: VerifyService,
    private val verifyServiceRepository: VerifyServiceRepository,
    private val emailService: MailService,
    private val emailFrom: String
) {
    fun execute(resendUserVerificationCommand: ResendUserVerificationCommand) {
        val currentUser = userRepository.getBy(resendUserVerificationCommand.userMail)
        val verification = verifyService.generate(VerificationType.VALIDATE_USER)
        verifyServiceRepository.save(UserVerification(currentUser, verification))
        emailService.send(
            ValidateEmailMailInformation(
                from = emailFrom,
                to = currentUser.email,
                validation = verification.verification
            )
        )

    }
}