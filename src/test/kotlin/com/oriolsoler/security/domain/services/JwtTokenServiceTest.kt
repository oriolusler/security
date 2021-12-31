package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.interfaces.DecodedJWT
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.domain.services.ClockService.Companion.localDateTimeToDate
import com.oriolsoler.security.domain.user.UserId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JwtTokenServiceTest {

    private val now = ClockService().now()
    private lateinit var clock: ClockService

    private val jwtKey = "8be5946d3b667b31b286ddd447c5cd948f7a6692e8603218d18e718344da323426d80aee0982b4a" +
            "7816953d5787ff2a868e46833a3575454b548c205cf9aa789"

    private val jwtIssuer = "NEA_OS"
    private lateinit var jwtTokenService: JwtTokenService

    @BeforeEach
    fun setUp() {
        clock = mock {
            on { nowDate() } doReturn localDateTimeToDate(now)
            on { nowDate(7) } doReturn localDateTimeToDate(now.plusDays(7))
        }

        jwtTokenService = JwtTokenService(jwtKey, jwtIssuer, clock)
    }

    @Test
    fun `should generate jwt token from token id`() {
        val userId = UserId()

        val jwtTokenGenerated = jwtTokenService.generate(userId, 7)

        val decodedToken = verifyAndDecodeJwtToken(jwtTokenGenerated.value)
        assertNotNull(decodedToken.expiresAt)
        assertNotNull(jwtTokenGenerated)
        assertEquals(userId.value, UserId(decodedToken.subject).value)
        assertEquals(jwtIssuer, decodedToken.issuer)
        assertEquals(getInstantWithoutMilis(localDateTimeToDate(now)), getInstantWithoutMilis(decodedToken.issuedAt))
        assertEquals(
            getInstantWithoutMilis(localDateTimeToDate(now.plusDays(7))),
            getInstantWithoutMilis(decodedToken.expiresAt)
        )
    }

    private fun getInstantWithoutMilis(date: Date): Instant {
        return date.toInstant().truncatedTo(ChronoUnit.SECONDS)
    }

    private fun verifyAndDecodeJwtToken(token: String): DecodedJWT {
        val algorithm: Algorithm = Algorithm.HMAC512(jwtKey)
        val verifier: JWTVerifier = JWT
            .require(algorithm)
            .build()

        return verifier.verify(token)
    }

    @Test
    fun `should get uuid from a valid jwt`() {
        val userId = UserId()
        val jwtTokenGenerated = jwtTokenService.generate(userId, 7)

        val result = jwtTokenService.validate(jwtTokenGenerated.value)

        assertEquals(userId.value.toString(), result)
    }

    @Test
    fun `should fail if issuer is not accepted`() {
        val userId = UserId()
        val generate = jwtTokenService.generate(userId, 7).value

        val jwtTokenServiceAux = JwtTokenService(jwtKey, "other", clock)
        val exception = assertThrows<InvalidClaimException> { jwtTokenServiceAux.validate(generate) }
        assertEquals("The Claim 'iss' value doesn't match the required issuer.", exception.message)
    }
}