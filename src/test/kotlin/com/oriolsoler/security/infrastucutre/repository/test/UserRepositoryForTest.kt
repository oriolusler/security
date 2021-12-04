package com.oriolsoler.security.infrastucutre.repository.test

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserId
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

    fun save(userId: UserId, email: String, password: String): User {
        val sql = """
           INSERT INTO SECURITY_USER(ID, NAME, EMAIL, PHONE, PASSWORD)
           VALUES (:id, :name, :email, :phone, :password)
       """.trimIndent()

        val newUser = User(email, "", "", password)
        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("id", userId.value)
        namedParameters.addValue("name", "General name")
        namedParameters.addValue("email", email)
        namedParameters.addValue("phone", "+34666118833")
        namedParameters.addValue("password", password)
        jdbcTemplate.update(sql, namedParameters)

        return newUser
    }
}