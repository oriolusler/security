package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.oriolsoler.security.application.accessverification.TokenVerification
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.user.UserId


class JwtTokenService(jwtKey: String, private val jwtIssuer: String) : TokenGenerator, TokenVerification {
    private val algorithm: Algorithm = Algorithm.HMAC512(jwtKey)

    override fun generate(userId: UserId): Token {
        return Token(
            JWT.create()
                .withPayload(setUpPayload(userId))
                .withIssuer(jwtIssuer)
                .sign(algorithm),
            "Bearer"
        )
    }

    override fun validate(token: String): String {
        val verifier: JWTVerifier = JWT
            .require(algorithm)
            .withIssuer(jwtIssuer)
            .build()

        val jwt: DecodedJWT = verifier.verify(token)
        return jwt.subject
    }

    private fun setUpPayload(userId: UserId): HashMap<String, String> {
        val payloadValue = HashMap<String, String>()
        payloadValue["sub"] = userId.value.toString()
        return payloadValue
    }

}