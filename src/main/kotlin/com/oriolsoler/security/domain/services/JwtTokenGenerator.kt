package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.oriolsoler.security.application.login.Token
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.user.UserId

class JwtTokenGenerator(private val jwtKey: String) : TokenGenerator {
    override fun generate(userId: UserId): Token {
        val algorithm: Algorithm = Algorithm.HMAC512(jwtKey)
        return Token(
            JWT.create()
                .withPayload(setUpPayload(userId))
                .sign(algorithm),
            "Bearer"
        )
    }

    private fun setUpPayload(userId: UserId): HashMap<String, String> {
        val payloadValue = HashMap<String, String>()
        payloadValue["sub"] = userId.value.toString()
        return payloadValue
    }

}