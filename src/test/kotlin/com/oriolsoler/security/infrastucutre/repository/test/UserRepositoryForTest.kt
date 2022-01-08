package com.oriolsoler.security.infrastucutre.repository.test

import com.oriolsoler.security.domain.user.User
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

    fun lock(user: User) {
        val sql = """
           UPDATE SECURITY_USER
           SET LOCKED = TRUE
           WHERE ID =:id
       """.trimIndent()

        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("id", user.id.value)
        jdbcTemplate.update(sql, namedParameters)
    }
}