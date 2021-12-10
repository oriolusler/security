package com.oriolsoler.security.infrastucutre.repository

import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.signup.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.domain.user.UserId
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

class PostgresVerifyRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val userRepository: LoginUserRepository
) : VerifyServiceRepository {
    override fun save(verification: Verification) {
        val sql = """
           INSERT INTO VERIFY(USER_ID, VERIFICATION, CREATION_DATE, EXPIRATION_DATE, USED)
           VALUES (:user, :verification, :creation_date, :expiration_date, :used)
       """.trimIndent()

        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("user", verification.user.id.value)
        namedParameters.addValue("verification", verification.verification)
        namedParameters.addValue("creation_date", verification.creationDate)
        namedParameters.addValue("expiration_date", verification.expirationDate)
        namedParameters.addValue("used", verification.used)
        jdbcTemplate.update(sql, namedParameters)
    }

    override fun getUnusedBy(user: User): Verification {
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

    private fun mapperVerification(): RowMapper<Verification> {
        return RowMapper { rs: ResultSet, _: Int ->
            val user = userRepository.getBy(UserId(rs.getString("user_id")))
            val verification = rs.getString("verification")
            val creationDate = rs.getTimestamp("creation_date").toLocalDateTime()
            val expirationDate = rs.getTimestamp("expiration_date").toLocalDateTime()
            val used = rs.getBoolean("used")
            Verification(
                user = user,
                verification = verification,
                creationDate = creationDate,
                expirationDate = expirationDate,
                used = used
            )
        }
    }

}