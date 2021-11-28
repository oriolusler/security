package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfiguration {
    @Bean
    fun signUpEmailPasswordUseCase(
        passwordEncoder: PasswordEncoder,
        userRepository: UserRepository
    ): SignUpEmailPasswordUseCase {
        return SignUpEmailPasswordUseCase(userRepository, passwordEncoder)
    }
}