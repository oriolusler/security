package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserRole
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

class PostgresUserRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : SignUpUserRepository, LoginUserRepository {
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

        newUser.roles.forEach { x ->
            val namedParameters = MapSqlParameterSource()
            namedParameters.addValue("userId", newUser.id.value)
            namedParameters.addValue("role", x.name)
            jdbcTemplate.update(sql, namedParameters)
        }
    }

    override fun getBy(email: String): User {
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
}