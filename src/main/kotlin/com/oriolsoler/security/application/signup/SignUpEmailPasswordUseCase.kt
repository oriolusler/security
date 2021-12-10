package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.springframework.security.crypto.password.PasswordEncoder

class SignUpEmailPasswordUseCase(
    private val signUpUserRepository: SignUpUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val verifyService: VerifyService,
    private val emailService: EmailService,
    private val verifyServiceRepository: VerifyServiceRepository
) {
    fun execute(signupRequestCommand: SignUpRequestCommand): User {
        val userCreated = signUpUserRepository.save(
            User(
                email = signupRequestCommand.email,
                password = passwordEncoder.encode(signupRequestCommand.password)
            )
        )
        val userVerification = UserVerification(userCreated, verifyService.generate())
        verifyServiceRepository.save(userVerification)
        emailService.send(userVerification.user.email, userVerification.verification.verification)
        return userCreated
    }
}
