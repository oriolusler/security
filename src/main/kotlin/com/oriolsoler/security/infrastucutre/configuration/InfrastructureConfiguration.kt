package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.EmailService
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.application.signup.VerifyService
import com.oriolsoler.security.application.signup.VerifyServiceRepository
import com.oriolsoler.security.domain.services.VerificationEmailService
import com.oriolsoler.security.domain.services.JwtTokenService
import com.oriolsoler.security.domain.services.PinVerifyService
import com.oriolsoler.security.infrastucutre.repository.PostgresUserRepository
import com.oriolsoler.security.infrastucutre.repository.PostgresVerifyRepository
import com.oriolsoler.security.infrastucutre.security.UserService
import com.oriolsoler.security.infrastucutre.security.filter.AuthTokenFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*


@Configuration
class InfrastructureConfiguration {
    @Bean
    fun signUpUserRepository(jdbcTemplate: NamedParameterJdbcTemplate): SignUpUserRepository {
        return PostgresUserRepository(jdbcTemplate)
    }

    @Bean
    fun loginUserRepository(jdbcTemplate: NamedParameterJdbcTemplate): LoginUserRepository {
        return PostgresUserRepository(jdbcTemplate)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun tokenGenerator(@Value("\${jwt.secret}") jwtKey: String): TokenGenerator {
        return JwtTokenService(jwtKey)
    }

    @Bean
    fun authenticationJwtTokenFilter(
        tokenGenerator: TokenGenerator,
        userDetailsService: UserDetailsService
    ): AuthTokenFilter {
        return AuthTokenFilter(tokenGenerator, userDetailsService)
    }

    @Bean
    fun userService(loginUserRepository: LoginUserRepository): UserDetailsService {
        return UserService(loginUserRepository)
    }

    @Bean
    fun verifyService(): VerifyService {
        return PinVerifyService()
    }

    @Bean
    fun emailService(
        javaMailSender: JavaMailSender,
        @Value("\${verifier-email.sender}") emailSender: String
    ): EmailService {
        return VerificationEmailService(javaMailSender, emailSender)
    }

    @Bean
    fun javaMailSender(
        @Value("\${verifier-email.from}") from: String,
        @Value("\${verifier-email.password}") password: String,
        @Value("\${verifier-email.host}") host: String,
        @Value("\${verifier-email.port}") port: Int
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
        userRepository: LoginUserRepository
    ): VerifyServiceRepository {
        return PostgresVerifyRepository(jdbcTemplate, userRepository)
    }
}