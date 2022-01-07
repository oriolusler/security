package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.oriolsoler.security.domain.services.ClockService.Companion.localDateTimeToDate
import com.oriolsoler.security.domain.services.exceptions.TokenValidationException
import com.oriolsoler.security.domain.user.UserId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private const val ACCESS_EXPIRATION_DAYS = 7L
private const val REFRESH_EXPIRATION_DAYS = 50L

class JwtTokenServiceTest {

    private val now = ClockService().now()
    private lateinit var clock: ClockService

    private val accessTokenKey = "8be5946d3b667b31b286ddd447c5cd948f7a6692e8603218d18e718344da323426" +
            "d80aee0982b4a7816953d5787ff2a868e46833a3575454b548c205cf9aa789"
    private val refreshTokenKey = "5c5f678f443188633a1ca92c4cb801ddd657febe8045c5e68288d15f435395a72a" +
            "b4978c62ff503bb750eee110bd4cac8a984eba38de7bd9b166ae1df98e8549"

    private val jwtIssuer = "NEA_OS"
    private lateinit var jwtTokenService: JwtTokenService

    @BeforeEach
    fun setUp() {
        clock = mock {
            on { nowDate() } doReturn localDateTimeToDate(now)
            on { nowDate(ACCESS_EXPIRATION_DAYS) } doReturn localDateTimeToDate(now.plusDays(ACCESS_EXPIRATION_DAYS))
            on { nowDate(REFRESH_EXPIRATION_DAYS) } doReturn localDateTimeToDate(now.plusDays(REFRESH_EXPIRATION_DAYS))
        }

        jwtTokenService = JwtTokenService(
            accessTokenKey = accessTokenKey,
            accessTokenKeyExpirationDays = ACCESS_EXPIRATION_DAYS,
            refreshTokenKey = refreshTokenKey,
            refreshTokenKeyExpirationDays = REFRESH_EXPIRATION_DAYS,
            jwtIssuer = jwtIssuer,
            clock = clock
        )
    }

    @Test
    fun `should generate jwt access token from token id`() {
        val userId = UserId()

        val jwtTokenGenerated = jwtTokenService.generate(userId)

        val decodedToken = verifyAndDecodeJwtAccessToken(jwtTokenGenerated.accessToken)
        assertNotNull(decodedToken.expiresAt)
        assertEquals(userId.value, UserId(decodedToken.subject).value)
        assertEquals(jwtIssuer, decodedToken.issuer)
        assertEquals(getInstantWithoutMilis(localDateTimeToDate(now)), getInstantWithoutMilis(decodedToken.issuedAt))
        assertEquals(
            getInstantWithoutMilis(localDateTimeToDate(now.plusDays(ACCESS_EXPIRATION_DAYS))),
            getInstantWithoutMilis(decodedToken.expiresAt)
        )
    }

    private fun getInstantWithoutMilis(date: Date): Instant {
        return date.toInstant().truncatedTo(ChronoUnit.SECONDS)
    }

    private fun verifyAndDecodeJwtAccessToken(token: String): DecodedJWT {
        val algorithm: Algorithm = Algorithm.HMAC512(accessTokenKey)
        val verifier: JWTVerifier = JWT
            .require(algorithm)
            .build()

        return verifier.verify(token)
    }

    private fun verifyAndDecodeJwtRefreshToken(token: String): DecodedJWT {
        val algorithm: Algorithm = Algorithm.HMAC512(refreshTokenKey)
        val verifier: JWTVerifier = JWT
            .require(algorithm)
            .build()

        return verifier.verify(token)
    }

    @Test
    fun `should get uuid from a valid jwt`() {
        val userId = UserId()
        val jwtTokenGenerated = jwtTokenService.generate(userId)

        val result = jwtTokenService.validateAccessToken(jwtTokenGenerated.accessToken)

        assertEquals(userId.value.toString(), result)
    }

    @Test
    fun `should fail if issuer is not accepted`() {
        val userId = UserId()
        val generate = jwtTokenService.generate(userId).accessToken

        val jwtTokenServiceAux = JwtTokenService(
            accessTokenKey = accessTokenKey,
            accessTokenKeyExpirationDays = ACCESS_EXPIRATION_DAYS,
            refreshTokenKey = refreshTokenKey,
            refreshTokenKeyExpirationDays = REFRESH_EXPIRATION_DAYS,
            jwtIssuer = "An other issuer",
            clock = clock
        )
        val exception = assertThrows<TokenValidationException> { jwtTokenServiceAux.validateAccessToken(generate) }
        assertEquals("Invalid token: The Claim 'iss' value doesn't match the required issuer.", exception.message)
    }

    @Test
    fun `access token should fail if token is expired`() {
        val userId = UserId()

        val clockAux = mock<ClockService> {
            on { nowDate() } doReturn localDateTimeToDate(now.minusDays(ACCESS_EXPIRATION_DAYS + 1))
            on { nowDate(ACCESS_EXPIRATION_DAYS) } doReturn localDateTimeToDate(now.minusDays(ACCESS_EXPIRATION_DAYS + 1))
            on { nowDate(REFRESH_EXPIRATION_DAYS) } doReturn localDateTimeToDate(now.plusDays(REFRESH_EXPIRATION_DAYS))
        }

        val jwtTokenServiceAux = JwtTokenService(
            accessTokenKey = accessTokenKey,
            accessTokenKeyExpirationDays = ACCESS_EXPIRATION_DAYS,
            refreshTokenKey = refreshTokenKey,
            refreshTokenKeyExpirationDays = REFRESH_EXPIRATION_DAYS,
            jwtIssuer = jwtIssuer,
            clock = clockAux
        )

        val generate = jwtTokenServiceAux.generate(userId).accessToken

        val exception = assertThrows<TokenValidationException> { jwtTokenServiceAux.validateAccessToken(generate) }
        assertContains(exception.message.toString(), "The Token has expired")
    }

    @Test
    fun `should creating refresh token from uuid`() {
        val userId = UserId()

        val jwtTokenGenerated = jwtTokenService.generate(userId)

        val decodedToken = verifyAndDecodeJwtRefreshToken(jwtTokenGenerated.refreshToken)
        assertNotNull(decodedToken.expiresAt)
        assertEquals(userId.value, UserId(decodedToken.subject).value)
        assertEquals(jwtIssuer, decodedToken.issuer)
        assertEquals(getInstantWithoutMilis(localDateTimeToDate(now)), getInstantWithoutMilis(decodedToken.issuedAt))
        assertEquals(
            getInstantWithoutMilis(localDateTimeToDate(now.plusDays(REFRESH_EXPIRATION_DAYS))),
            getInstantWithoutMilis(decodedToken.expiresAt)
        )
    }


    @Test
    fun `refresh token should fail if token is expired`() {
        val userId = UserId()

        val clockAux = mock<ClockService> {
            on { nowDate() } doReturn localDateTimeToDate(now.minusDays(10))
            on { nowDate(ACCESS_EXPIRATION_DAYS) } doReturn localDateTimeToDate(now.plusDays(ACCESS_EXPIRATION_DAYS))
            on { nowDate(REFRESH_EXPIRATION_DAYS) } doReturn localDateTimeToDate(now.minusDays(REFRESH_EXPIRATION_DAYS))
        }

        val jwtTokenServiceAux = JwtTokenService(
            accessTokenKey = accessTokenKey,
            accessTokenKeyExpirationDays = ACCESS_EXPIRATION_DAYS,
            refreshTokenKey = refreshTokenKey,
            refreshTokenKeyExpirationDays = REFRESH_EXPIRATION_DAYS,
            jwtIssuer = jwtIssuer,
            clock = clockAux
        )

        val generate = jwtTokenServiceAux.generate(userId).refreshToken

        val exception = assertThrows<TokenValidationException> { jwtTokenServiceAux.validateRefreshToken(generate) }
        assertContains(exception.message.toString(), "The Token has expired")
    }
}