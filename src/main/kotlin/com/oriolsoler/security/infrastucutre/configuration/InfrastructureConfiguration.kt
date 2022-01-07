package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.accessverification.TokenVerification
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.services.*
import com.oriolsoler.security.infrastucutre.repository.user.PostgresUserRepository
import com.oriolsoler.security.infrastucutre.repository.verification.PostgresVerifyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Properties


@Configuration
class InfrastructureConfiguration {
    @Bean
    fun userRepository(jdbcTemplate: NamedParameterJdbcTemplate): UserRepository {
        return PostgresUserRepository(jdbcTemplate)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun tokenGenerator(
        @Value("\${jwt.access.secret}") accessSecret: String,
        @Value("\${jwt.access.expirationDays}") accessExpiration: Long,
        @Value("\${jwt.refresh.secret}") refreshSecret: String,
        @Value("\${jwt.refresh.expirationDays}") refreshExpiration: Long,
        @Value("\${jwt.issuer}") jwtIssuer: String,
        clock: ClockService
    ): TokenGenerator {
        return JwtTokenService(
            accessTokenKey = accessSecret,
            accessTokenKeyExpirationDays = accessExpiration,
            refreshTokenKey = refreshSecret,
            refreshTokenKeyExpirationDays = refreshExpiration,
            jwtIssuer = jwtIssuer,
            clock = clock
        )
    }

    @Bean
    fun tokenVerification(
        @Value("\${jwt.access.secret}") accessSecret: String,
        @Value("\${jwt.access.expirationDays}") accessExpiration: Long,
        @Value("\${jwt.refresh.secret}") refreshSecret: String,
        @Value("\${jwt.refresh.expirationDays}") refreshExpiration: Long,
        @Value("\${jwt.issuer}") jwtIssuer: String,
        clock: ClockService
    ): TokenVerification {
        return JwtTokenService(
            accessTokenKey = accessSecret,
            accessTokenKeyExpirationDays = accessExpiration,
            refreshTokenKey = refreshSecret,
            refreshTokenKeyExpirationDays = refreshExpiration,
            jwtIssuer = jwtIssuer,
            clock = clock
        )
    }

    @Bean
    fun verifyService(
        clockService: ClockService,
        @Value("\${verification.valid-minutes}") minutesValid: Long
    ): VerifyService {
        return PinVerifyService(clockService, minutesValid)
    }

    @Bean
    fun clockService(): ClockService {
        return ClockService()
    }

    @Bean("emailVerificationService")
    fun emailService(
        javaMailSender: JavaMailSender
    ): MailService {
        return MailSenderService(javaMailSender)
    }

    @Bean
    fun javaMailSender(
        @Value("\${verification.email.auth-enabled}") authEnabled: Boolean,
        @Value("\${verification.email.from}") from: String,
        @Value("\${verification.email.password}") password: String,
        @Value("\${verification.email.host}") host: String,
        @Value("\${verification.email.port}") port: Int
    ): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        if (authEnabled) {
            mailSender.username = from
            mailSender.password = password
        }
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = authEnabled.toString()
        props["mail.smtp.starttls.enable"] = authEnabled.toString()
        props["mail.debug"] = "true"
        return mailSender
    }

    @Bean
    fun verifyServiceRepository(
        jdbcTemplate: NamedParameterJdbcTemplate,
        userRepository: UserRepository
    ): VerifyServiceRepository {
        return PostgresVerifyRepository(jdbcTemplate, userRepository)
    }

    @Bean
    fun passwordService(
        passwordEncoder: PasswordEncoder
    ): PasswordService {
        return SpringEncoderPasswordService(passwordEncoder)
    }
}