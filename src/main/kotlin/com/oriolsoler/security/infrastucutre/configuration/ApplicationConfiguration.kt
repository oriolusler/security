package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.SignUpUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfiguration {
    @Bean
    fun signUpEmailPasswordUseCase(
        passwordEncoder: PasswordEncoder,
        signUpUserRepository: SignUpUserRepository
    ): SignUpEmailPasswordUseCase {
        return SignUpEmailPasswordUseCase(signUpUserRepository, passwordEncoder)
    }
}