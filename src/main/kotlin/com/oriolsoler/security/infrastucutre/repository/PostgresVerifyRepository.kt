package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.signup.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.domain.user.UserId
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

class PostgresVerifyRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val userRepository: UserRepository
) : VerifyServiceRepository {
    override fun save(userVerification: UserVerification) {
        val sql = """
           INSERT INTO VERIFY(USER_ID, VERIFICATION, CREATION_DATE, EXPIRATION_DATE, USED)
           VALUES (:user, :verification, :creation_date, :expiration_date, :used)
       """.trimIndent()

        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("user", userVerification.user.id.value)
        namedParameters.addValue("verification", userVerification.verification.verification)
        namedParameters.addValue("creation_date", userVerification.verification.creationDate)
        namedParameters.addValue("expiration_date", userVerification.verification.expirationDate)
        namedParameters.addValue("used", userVerification.verification.used)
        jdbcTemplate.update(sql, namedParameters)
    }

    override fun getUnusedBy(user: User): UserVerification {
        val query = """
            SELECT USER_ID, VERIFICATION, CREATION_DATE, EXPIRATION_DATE, USED
             FROM VERIFY
             WHERE USER_ID=:user
             AND used=FALSE
        """.trimIndent()

        val namedParameter = MapSqlParameterSource()
        namedParameter.addValue("user", user.id.value)

        return try {
            jdbcTemplate.queryForObject(query, namedParameter, mapperVerification())!!
        } catch (exception: EmptyResultDataAccessException) {
            throw Exception("Empty USER result")
        }
    }

    override fun getUnusedBy(user: User, verification: String): UserVerification {
        val query = """
            SELECT USER_ID, VERIFICATION, CREATION_DATE, EXPIRATION_DATE, USED
             FROM VERIFY
             WHERE USER_ID=:user
             AND VERIFICATION=:verification
        """.trimIndent()

        val namedParameter = MapSqlParameterSource()
        namedParameter.addValue("user", user.id.value)
        namedParameter.addValue("verification", verification)

        return try {
            jdbcTemplate.queryForObject(query, namedParameter, mapperVerification())!!
        } catch (exception: EmptyResultDataAccessException) {
            throw Exception("Empty USER result")
        }
    }

    override fun setToUsed(userVerification: UserVerification) {
        val query = """
            UPDATE VERIFY
             SET USED=TRUE
             WHERE USER_ID=:user
              AND VERIFICATION=:verification
        """.trimIndent()

        val namedParameter = MapSqlParameterSource()
        namedParameter.addValue("user", userVerification.user.id.value)
        namedParameter.addValue("verification", userVerification.verification.verification)

        jdbcTemplate.update(query, namedParameter)
    }

    private fun mapperVerification(): RowMapper<UserVerification> {
        return RowMapper { rs: ResultSet, _: Int ->
            val user = userRepository.getBy(UserId(rs.getString("user_id")))
            val verification = rs.getString("verification")
            val creationDate = rs.getTimestamp("creation_date").toLocalDateTime()
            val expirationDate = rs.getTimestamp("expiration_date").toLocalDateTime()
            val used = rs.getBoolean("used")
            UserVerification(
                user = user,
                Verification(
                    verification = verification,
                    creationDate = creationDate,
                    expirationDate = expirationDate,
                    used = used
                )
            )
        }
    }

}