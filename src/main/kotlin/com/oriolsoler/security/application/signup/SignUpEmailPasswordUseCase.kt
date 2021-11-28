package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.springframework.security.crypto.password.PasswordEncoder

class SignUpEmailPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun execute(signupRequestCommand: SignUpRequestCommand): User {
        return userRepository.save(
            signupRequestCommand.email,
            passwordEncoder.encode(signupRequestCommand.password)
        )
    }
}
