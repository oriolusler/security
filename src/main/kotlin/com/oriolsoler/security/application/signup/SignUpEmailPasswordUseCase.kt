package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.springframework.security.crypto.password.PasswordEncoder

class SignUpEmailPasswordUseCase(
    private val signUpUserRepository: SignUpUserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun execute(signupRequestCommand: SignUpRequestCommand): User {
        return signUpUserRepository.save(
            signupRequestCommand.email,
            passwordEncoder.encode(signupRequestCommand.password)
        )
    }
}
