package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import com.oriolsoler.security.infrastucutre.repository.test.VerificationRepositoryForTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class TestConfiguration {
    @Bean
    fun userRepositoryForTest(
        namedParameterJdbcTemplate: NamedParameterJdbcTemplate
    ): UserRepositoryForTest = UserRepositoryForTest(namedParameterJdbcTemplate)

    @Bean
    fun verificationRepositoryForTest(
        namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
        userRepository: UserRepository
    ): VerificationRepositoryForTest = VerificationRepositoryForTest(namedParameterJdbcTemplate, userRepository)
}