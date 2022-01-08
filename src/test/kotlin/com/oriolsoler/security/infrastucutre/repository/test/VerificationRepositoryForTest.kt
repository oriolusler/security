package com.oriolsoler.security.infrastucutre.repository.test

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.verification.UserVerification
import com.oriolsoler.security.domain.verification.Verification
import com.oriolsoler.security.domain.user.UserId
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

class VerificationRepositoryForTest(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val userRepository: UserRepository
) {
    fun clean() {
        val query = """
            TRUNCATE VERIFY CASCADE;
        """.trimIndent()

        val emptyNamedParameter = MapSqlParameterSource()
        jdbcTemplate.update(query, emptyNamedParameter)
    }

    fun getBy(userVerification: UserVerification): UserVerification {
        val query = """
            SELECT USER_ID, VERIFICATION, CREATION_DATE, EXPIRATION_DATE, USED
             FROM VERIFY
             WHERE USER_ID=:user
             AND VERIFICATION=:verification
        """.trimIndent()

        val namedParameter = MapSqlParameterSource()
        namedParameter.addValue("user", userVerification.user.id.value)
        namedParameter.addValue("verification", userVerification.verification.verification)

        return try {
            jdbcTemplate.queryForObject(query, namedParameter, mapperVerification())!!
        } catch (exception: EmptyResultDataAccessException) {
            throw Exception("No verification found")
        }
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