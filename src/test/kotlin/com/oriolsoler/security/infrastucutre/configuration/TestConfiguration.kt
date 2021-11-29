package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
class TestConfiguration {
    @Bean
    fun userRepositoryForTest(
        namedParameterJdbcTemplate: NamedParameterJdbcTemplate
    ): UserRepositoryForTest = UserRepositoryForTest(namedParameterJdbcTemplate)
}