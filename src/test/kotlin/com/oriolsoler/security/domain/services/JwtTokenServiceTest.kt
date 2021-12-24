package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.interfaces.DecodedJWT
import com.oriolsoler.security.domain.user.UserId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JwtTokenServiceTest {

    private val jwtKey = "8be5946d3b667b31b286ddd447c5cd948f7a6692e8603218d18e718344da323426d80aee0982b4a" +
            "7816953d5787ff2a868e46833a3575454b548c205cf9aa789"

    private val jwtIssuer = "NEA_OS"
    private val jwtTokenService = JwtTokenService(jwtKey, jwtIssuer)

    @Test
    fun `should generate jwt token from token id`() {
        val userId = UserId()

        val jwtTokenGenerated = jwtTokenService.generate(userId)

        val decodedToken = verifyAndDecodeJwtToken(jwtTokenGenerated.value)
        assertNotNull(jwtTokenGenerated)
        assertEquals(userId.value, UserId(decodedToken.subject).value)
        assertEquals(jwtIssuer, decodedToken.issuer)
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
        val jwtTokenGenerated = jwtTokenService.generate(userId)

        val result = jwtTokenService.validate(jwtTokenGenerated.value)

        assertEquals(userId.value.toString(), result)
    }

    @Test
    fun `should fail if issuer is not accepted`() {
        val userId = UserId()
        val generate = jwtTokenService.generate(userId).value

        val jwtTokenServiceAux = JwtTokenService(jwtKey, "other")
        val exception = assertThrows<InvalidClaimException> { jwtTokenServiceAux.validate(generate) }
        assertEquals("The Claim 'iss' value doesn't match the required issuer.", exception.message)
    }
}