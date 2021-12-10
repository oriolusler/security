package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfiguration {
    @Bean
    fun signUpEmailPasswordUseCase(
        passwordEncoder: PasswordEncoder,
        signUpUserRepository: SignUpUserRepository,
        verifyService: VerifyService,
        emailService: EmailService,
        verifyServiceRepository: VerifyServiceRepository
    ): SignUpEmailPasswordUseCase {
        return SignUpEmailPasswordUseCase(
            signUpUserRepository,
            passwordEncoder,
            verifyService,
            emailService,
            verifyServiceRepository
        )
    }

    @Bean
    fun loginEmailPasswordUserCase(
        loginUserRepository: LoginUserRepository,
        passwordEncoder: PasswordEncoder,
        tokenGenerator: TokenGenerator
    ): LoginEmailPasswordUseCase {
        return LoginEmailPasswordUseCase(loginUserRepository, passwordEncoder, tokenGenerator)
    }
}