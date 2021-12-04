package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.oriolsoler.security.application.login.Token
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.user.UserId


class JwtTokenService(private val jwtKey: String) : TokenGenerator {
    override fun generate(userId: UserId): Token {
        val algorithm: Algorithm = Algorithm.HMAC512(jwtKey)
        return Token(
            JWT.create()
                .withPayload(setUpPayload(userId))
                .sign(algorithm),
            "Bearer"
        )
    }

    //TODO Test
    override fun getUserIdFromToken(token: String): String {
        val algorithm: Algorithm = Algorithm.HMAC512(jwtKey)
        val verifier: JWTVerifier = JWT
            .require(algorithm)
            .build()

        val jwt: DecodedJWT = verifier.verify(token)
        return jwt.subject
    }

    //TODO Test
    override fun isValid(token: String): Boolean {
        return getUserIdFromToken(token).isNotEmpty()
    }

    private fun setUpPayload(userId: UserId): HashMap<String, String> {
        val payloadValue = HashMap<String, String>()
        payloadValue["sub"] = userId.value.toString()
        return payloadValue
    }

}