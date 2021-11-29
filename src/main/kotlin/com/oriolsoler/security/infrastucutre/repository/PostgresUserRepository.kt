package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.domain.User
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class PostgresUserRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : SignUpUserRepository {
    override fun save(email: String, password: String): User {
        val sql = """
           INSERT INTO SECURITY_USER(ID, NAME, EMAIL, PHONE, PASSWORD)
           VALUES (:id, :name, :email, :phone, :password)
       """.trimIndent()

        val newUser = User(email, "", "", password)
        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("id", newUser.id!!.value)
        namedParameters.addValue("name", "General name")
        namedParameters.addValue("email", email)
        namedParameters.addValue("phone", "+34666118833")
        namedParameters.addValue("password", password)
        jdbcTemplate.update(sql, namedParameters)

        saveRoles(newUser)
        return newUser
    }

    private fun saveRoles(newUser: User) {
        val sql = """
           INSERT INTO SECURITY_USER_ROLE(USER_ID, ROLE)
           VALUES (:userId, :role)
       """.trimIndent()

        newUser.roles!!.forEach { x ->
            val namedParameters = MapSqlParameterSource()
            namedParameters.addValue("userId", newUser.id!!.value)
            namedParameters.addValue("role", x.name)
            jdbcTemplate.update(sql, namedParameters)
        }
    }
}