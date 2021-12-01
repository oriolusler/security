package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.domain.services.JwtTokenGenerator
import com.oriolsoler.security.infrastucutre.repository.PostgresUserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

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
        return JwtTokenGenerator(jwtKey)
    }
}