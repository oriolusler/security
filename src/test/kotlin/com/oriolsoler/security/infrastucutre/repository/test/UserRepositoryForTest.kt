package com.oriolsoler.security.infrastucutre.repository.test

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class UserRepositoryForTest(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    fun clean() {
        val query = """
            TRUNCATE SECURITY_USER CASCADE;
        """.trimIndent()

        val emptyNamedParameter = MapSqlParameterSource()
        jdbcTemplate.update(query, emptyNamedParameter)
    }
}