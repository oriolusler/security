package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.springframework.security.crypto.password.PasswordEncoder

class SignUpEmailPasswordUseCase(
    private val signUpUserRepository: SignUpUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val verifyService: VerifyService,
    private val emailService: EmailService
) {
    fun execute(signupRequestCommand: SignUpRequestCommand): User {
        val userCreated = signUpUserRepository.save(
            User(
                email = signupRequestCommand.email,
                password = passwordEncoder.encode(signupRequestCommand.password),
                name = signupRequestCommand.name,
                phone = signupRequestCommand.phone,
                roles = signupRequestCommand.roles
            )
        )
        emailService.send(userCreated.email, verifyService.generate())
        return userCreated
    }
}
