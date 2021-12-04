package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.oriolsoler.security.domain.user.UserId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JwtTokenServiceTest {

    private val jwtKey = "8be5946d3b667b31b286ddd447c5cd948f7a6692e8603218d18e718344da323426d80aee0982b4a" +
            "7816953d5787ff2a868e46833a3575454b548c205cf9aa789"

    private val jwtTokenService = JwtTokenService(jwtKey)

    @Test
    fun `should generate jwt token from token id`() {
        val userId = UserId()

        val jwtTokenGenerated = jwtTokenService.generate(userId)

        val decodedToken = verifyAndDecodeJwtToken(jwtTokenGenerated.value)
        assertNotNull(jwtTokenGenerated)
        assertEquals(userId.value, decodedToken.value)
    }

    private fun verifyAndDecodeJwtToken(token: String): UserId {
        val algorithm: Algorithm = Algorithm.HMAC512(jwtKey)
        val verifier: JWTVerifier = JWT
            .require(algorithm)
            .build()

        val jwt: DecodedJWT = verifier.verify(token)
        val tokenSub = jwt.subject
        return UserId(tokenSub)
    }

    @Test
    fun `should get uuid from a valid jwt`() {
        val expectedUuid = "b6c2458d-652c-4061-95e7-4f47b0de2b94"
        val jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiNmMyNDU4ZC02NTJjLTQwNjEtOTVlNy" +
                "00ZjQ3YjBkZTJiOTQifQ.BYGZ26WmXqSi5GWdKCGXzIjebofEZcIfSOQYqYuXkbqlFnHgZGXQrTRyVxi9BRypibNgrGKnspMM-eIayx1Rmw"

        val result = jwtTokenService.getUserIdFromToken(jwt)
        val isValidJwt = jwtTokenService.isValid(jwt)

        assertTrue(isValidJwt)
        assertEquals(expectedUuid, result)
    }
}