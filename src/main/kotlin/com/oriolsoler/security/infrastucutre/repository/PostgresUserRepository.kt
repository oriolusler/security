package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.application.UserRepository
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
) : UserRepository {
    override fun save(user: User): User {
        val sql = """
           INSERT INTO SECURITY_USER(ID, EMAIL, PASSWORD, ENABLED, LOCKED)
           VALUES (:id, :email, :password, :enabled, :locked)
       """.trimIndent()

        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("id", user.id.value)
        namedParameters.addValue("email", user.email)
        namedParameters.addValue("password", user.password)
        namedParameters.addValue("enabled", user.enabled)
        namedParameters.addValue("locked", user.locked)
        jdbcTemplate.update(sql, namedParameters)

        saveRoles(user)
        return user
    }

    override fun setUnlocked(user: User) {
        val sql = """
           UPDATE SECURITY_USER
           SET LOCKED = FALSE
           WHERE ID =:id
       """.trimIndent()

        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("id", user.id.value)
        jdbcTemplate.update(sql, namedParameters)
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
            SELECT ID, EMAIL, PASSWORD, ENABLED, LOCKED
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

    override fun getBy(userId: UserId): User {
        val query = """
            SELECT ID, EMAIL, PASSWORD, ENABLED, LOCKED
             FROM SECURITY_USER
             WHERE ID=:id
        """.trimIndent()

        val namedParameter = MapSqlParameterSource()
        namedParameter.addValue("id", userId.value)

        return try {
            jdbcTemplate.queryForObject(query, namedParameter, mapperUser())!!
        } catch (exception: EmptyResultDataAccessException) {
            throw Exception("Empty USER result")
        }
    }

    private fun mapperUser(): RowMapper<User> {
        return RowMapper { rs: ResultSet, _: Int ->
            val id = UserId(rs.getString("id"))
            val email = rs.getString("email")
            val password = rs.getString("password")
            val enabled = rs.getBoolean("enabled")
            val locked = rs.getBoolean("locked")
            val roles = getUserRolls(id)
            User(id = id, email = email, password = password, roles = roles, enabled = enabled, locked = locked)
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