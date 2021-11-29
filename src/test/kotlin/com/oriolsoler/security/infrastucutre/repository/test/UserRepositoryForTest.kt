package com.oriolsoler.security.infrastucutre.repository.test

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

class UserRepositoryForTest(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun findBy(email: String): User {
        val query = """
            SELECT ID, NAME, EMAIL, PHONE, PASSWORD
             FROM SECURITY_USER
             WHERE EMAIL=:email
        """.trimIndent()

        val namedParameter = MapSqlParameterSource()
        namedParameter.addValue("email", email)

        return try {
            jdbcTemplate.queryForObject(query, namedParameter, mapperUser())!!
        } catch (exception: EmptyResultDataAccessException) {
            throw Exception("Empty USER result")
        }
    }

    private fun mapperUser(): RowMapper<User> {
        return RowMapper { rs: ResultSet, _: Int ->
            val id = UserId(rs.getString("id"))
            val name = rs.getString("name")
            val email = rs.getString("email")
            val phone = rs.getString("phone")
            val password = rs.getString("password")
            val roles = getUserRolls(id)
            User(id, name, email, phone, password, roles)
        }
    }

    private fun getUserRolls(id: UserId): List<UserRole> {
        val query = """
            SELECT ROLE
             FROM SECURITY_USER_ROLE
             Where USER_ID=:userId
        """.trimIndent()

        val namedParameter = MapSqlParameterSource()
        namedParameter.addValue("userId", id.value)

        return jdbcTemplate.query(query, namedParameter, mapperUserRole())
    }

    private fun mapperUserRole(): RowMapper<UserRole> {
        return RowMapper { rs: ResultSet, _: Int ->
            UserRole.valueOf(rs.getString("role"))
        }
    }

    fun clean() {
        val query = """
            TRUNCATE SECURITY_USER CASCADE;
        """.trimIndent()

        val emptyNamedParameter = MapSqlParameterSource()
        jdbcTemplate.update(query, emptyNamedParameter)

        //cleanRoles()
    }

    private fun cleanRoles() {
        val query = """
            TRUNCATE SECURITY_USER_ROLE;
        """.trimIndent()

        val emptyNamedParameter = MapSqlParameterSource()
        jdbcTemplate.update(query, emptyNamedParameter)
    }
}