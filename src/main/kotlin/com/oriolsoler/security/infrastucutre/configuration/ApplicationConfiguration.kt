package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.accessverification.AccessVerificationUseCase
import com.oriolsoler.security.application.accessverification.TokenVerification
import com.oriolsoler.security.application.forgotpassword.ForgotPasswordUseCase
import com.oriolsoler.security.application.updatepassword.UpdatePasswordUseCase
import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenUseCase
import com.oriolsoler.security.application.VerifyService
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.application.resenduserverification.ResendUserVerificationUseCase
import com.oriolsoler.security.application.validateupdatepassword.ValidateUpdatePasswordUseCase
import com.oriolsoler.security.application.validateuser.ValidateUserUseCase
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {
    @Bean
    fun signUpEmailPasswordUseCase(
        passwordService: PasswordService,
        userRepository: UserRepository,
        verifyService: VerifyService,
        @Qualifier("emailVerificationService") emailService: MailService,
        verifyServiceRepository: VerifyServiceRepository,
        @Value("\${verification.email.from}") emailSender: String
    ): SignUpEmailPasswordUseCase {
        return SignUpEmailPasswordUseCase(
            userRepository,
            passwordService,
            verifyService,
            emailService,
            verifyServiceRepository,
            emailSender
        )
    }

    @Bean
    fun loginEmailPasswordUserCase(
        userRepository: UserRepository,
        passwordService: PasswordService,
        tokenGenerator: TokenGenerator
    ): LoginEmailPasswordUseCase {
        return LoginEmailPasswordUseCase(userRepository, passwordService, tokenGenerator)
    }

    @Bean
    fun validateUpdatePasswordUseCase(
        verifyService: VerifyService,
        verifyServiceRepository: VerifyServiceRepository,
        userRepository: UserRepository
    ): ValidateUpdatePasswordUseCase {
        return ValidateUpdatePasswordUseCase(verifyService, verifyServiceRepository, userRepository)
    }

    @Bean
    fun validateUserUseCase(
        verifyService: VerifyService,
        verifyServiceRepository: VerifyServiceRepository,
        userRepository: UserRepository
    ): ValidateUserUseCase {
        return ValidateUserUseCase(verifyService, verifyServiceRepository, userRepository)
    }

    @Bean
    fun accessVerificationUseCase(
        tokenVerification: TokenVerification,
        userRepository: UserRepository
    ): AccessVerificationUseCase {
        return AccessVerificationUseCase(tokenVerification, userRepository)
    }

    @Bean
    fun validateRefreshTokenUseCase(
        tokenVerification: TokenVerification,
        userRepository: UserRepository,
        tokenGenerator: TokenGenerator
    ): ValidateRefreshTokenUseCase {
        return ValidateRefreshTokenUseCase(tokenVerification, userRepository, tokenGenerator)
    }

    @Bean
    fun forgotPasswordUseCase(
        verifyService: VerifyService,
        emailService: MailService,
        verifyServiceRepository: VerifyServiceRepository,
        userRepository: UserRepository,
        @Value("\${verification.email.from}") emailSender: String
    ): ForgotPasswordUseCase {
        return ForgotPasswordUseCase(verifyService, emailService, verifyServiceRepository, userRepository, emailSender)
    }

    @Bean
    fun updatePasswordUseCase(
        userRepository: UserRepository,
        verifyService: VerifyService,
        verifyServiceRepository: VerifyServiceRepository,
        passwordService: PasswordService
    ): UpdatePasswordUseCase {
        return UpdatePasswordUseCase(userRepository, verifyService, verifyServiceRepository, passwordService)
    }

    @Bean
    fun resendUserVerificationUseCase(
        userRepository: UserRepository,
        verifyService: VerifyService,
        verifyServiceRepository: VerifyServiceRepository,
        emailService: MailService,
        @Value("\${verification.email.from}") emailSender: String
    ): ResendUserVerificationUseCase {
        return ResendUserVerificationUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            emailService,
            emailSender
        )
    }
}