package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.*
import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfiguration {
    @Bean
    fun signUpEmailPasswordUseCase(
        passwordEncoder: PasswordEncoder,
        userRepository: UserRepository,
        verifyService: VerifyService,
        emailService: EmailService,
        verifyServiceRepository: VerifyServiceRepository
    ): SignUpEmailPasswordUseCase {
        return SignUpEmailPasswordUseCase(
            userRepository,
            passwordEncoder,
            verifyService,
            emailService,
            verifyServiceRepository
        )
    }

    @Bean
    fun loginEmailPasswordUserCase(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
        tokenGenerator: TokenGenerator
    ): LoginEmailPasswordUseCase {
        return LoginEmailPasswordUseCase(userRepository, passwordEncoder, tokenGenerator)
    }

    @Bean
    fun verifyVerificationUseCase(
        verifyService: VerifyService,
        verifyServiceRepository: VerifyServiceRepository,
        userRepository: UserRepository
    ): VerifyVerificationUseCase {
        return VerifyVerificationUseCase(verifyService, verifyServiceRepository, userRepository)
    }
}