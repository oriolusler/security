package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.accessverification.TokenVerification
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.EmailService
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.services.ClockService
import com.oriolsoler.security.domain.services.JwtTokenService
import com.oriolsoler.security.domain.services.PinVerifyService
import com.oriolsoler.security.domain.services.VerificationEmailService
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
        @Value("\${jwt.secret}") jwtKey: String,
        @Value("\${jwt.issuer}") jwtIssuer: String,
        clock: ClockService
    ): TokenGenerator {
        return JwtTokenService(jwtKey, jwtIssuer, clock)
    }

    @Bean
    fun tokenVerification(
        @Value("\${jwt.secret}") jwtKey: String,
        @Value("\${jwt.issuer}") jwtIssuer: String,
        clock: ClockService
    ): TokenVerification {
        return JwtTokenService(jwtKey, jwtIssuer, clock)
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

    @Bean
    fun emailService(
        javaMailSender: JavaMailSender,
        @Value("\${verification.email.from}") emailSender: String
    ): EmailService {
        return VerificationEmailService(javaMailSender, emailSender)
    }

    @Bean
    fun javaMailSender(
        @Value("\${verification.email.from}") from: String,
        @Value("\${verification.email.password}") password: String,
        @Value("\${verification.email.host}") host: String,
        @Value("\${verification.email.port}") port: Int
    ): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port
        mailSender.username = from
        mailSender.password = password
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
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
}