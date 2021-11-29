package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.signup.UserRepository
import com.oriolsoler.security.infrastucutre.repository.PostgresUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

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
}