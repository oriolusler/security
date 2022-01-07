package com.oriolsoler.security.application.signup

import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.email.ValidateEmailMailInformation
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserAlreadyExistsException

class SignUpEmailPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val verifyService: VerifyService,
    private val mailService: MailService,
    private val verifyServiceRepository: VerifyServiceRepository,
    private val mailFrom: String
) {
    fun execute(signupRequestCommand: SignUpRequestCommand): User {
        val userCreated = saveNewUser(signupRequestCommand)
        val userVerification = UserVerification(userCreated, verifyService.generate())
        verifyServiceRepository.save(userVerification)
        sendMail(userVerification)
        return userCreated
    }

    private fun sendMail(userVerification: UserVerification) {
        val validateEmailMailInformation = ValidateEmailMailInformation(
            from = mailFrom,
            to = userVerification.user.email,
            validation = userVerification.verification.verification
        )
        mailService.send(validateEmailMailInformation)
    }

    private fun saveNewUser(signupRequestCommand: SignUpRequestCommand): User {
        checkIfUserAlreadyExists(signupRequestCommand.email)

        return userRepository.save(
            User(
                email = signupRequestCommand.email,
                password = passwordService.encode(signupRequestCommand.password)
            )
        )
    }

    private fun checkIfUserAlreadyExists(email: String) {
        try {
            userRepository.checkIfUserAlreadyExists(email)
        } catch (e: UserAlreadyExistsException) {
            throw SignUpException(e.message, e)
        }
    }
}
