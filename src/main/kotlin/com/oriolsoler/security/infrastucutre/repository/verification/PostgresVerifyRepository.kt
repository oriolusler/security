package com.oriolsoler.security.infrastucutre.repository.verification

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.Verification
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
           INSERT INTO VERIFY(USER_ID, VERIFICATION, CREATION_DATE, EXPIRATION_DATE, USED, DELETED)
           VALUES (:user, :verification, :creation_date, :expiration_date, :used, :deleted)
       """.trimIndent()

        val namedParameters = MapSqlParameterSource()
        namedParameters.addValue("user", userVerification.user.id.value)
        namedParameters.addValue("verification", userVerification.verification.verification)
        namedParameters.addValue("creation_date", userVerification.verification.creationDate)
        namedParameters.addValue("expiration_date", userVerification.verification.expirationDate)
        namedParameters.addValue("used", userVerification.verification.used)
        namedParameters.addValue("deleted", userVerification.verification.deleted)
        jdbcTemplate.update(sql, namedParameters)
    }

    override fun getBy(user: User, verification: String): UserVerification {
        val query = """
                SELECT USER_ID, VERIFICATION, CREATION_DATE, EXPIRATION_DATE, USED, DELETED
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
            throw VerificationNotFoundException()
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

    override fun setToDeleted(userVerification: UserVerification) {
        val query = """
            UPDATE VERIFY
             SET DELETED=TRUE
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
            val deleted = rs.getBoolean("deleted")
            UserVerification(
                user = user,
                Verification(
                    verification = verification,
                    creationDate = creationDate,
                    expirationDate = expirationDate,
                    used = used,
                    deleted = deleted
                )
            )
        }
    }

}