package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.application.signup.UserRepository
import com.oriolsoler.security.domain.User
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PostgresUserRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : UserRepository {
    override fun save(email: String, password: String): User {
        TODO("Not yet implemented")
    }
}