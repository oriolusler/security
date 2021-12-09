package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.EmailService
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.application.signup.VerifyService
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
        emailService: EmailService
    ): SignUpEmailPasswordUseCase {
        return SignUpEmailPasswordUseCase(signUpUserRepository, passwordEncoder, verifyService, emailService)
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