package com.oriolsoler.security.infrastucutre.repository.test

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class VerificationRepositoryForTest(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun clean() {
        val query = """
            TRUNCATE VERIFY CASCADE;
        """.trimIndent()

        val emptyNamedParameter = MapSqlParameterSource()
        jdbcTemplate.update(query, emptyNamedParameter)
    }
}