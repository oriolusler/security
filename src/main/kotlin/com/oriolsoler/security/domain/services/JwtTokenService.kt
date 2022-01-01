package com.oriolsoler.security.domain.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.oriolsoler.security.application.accessverification.TokenVerification
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.user.UserId


class JwtTokenService(
    accessTokenKey: String,
    private val jwtIssuer: String,
    private val clock: ClockService,
    refreshTokenKey: String,
    private val accessTokenKeyExpirationDays: Long,
    private val refreshTokenKeyExpirationDays: Long
) : TokenGenerator, TokenVerification {

    private val accessTokenAlgorithm: Algorithm = Algorithm.HMAC512(accessTokenKey)
    private val refreshTokenAlgorithm: Algorithm = Algorithm.HMAC512(refreshTokenKey)

    override fun generate(userId: UserId): Token {
        return Token(
            generateAccessToken(userId),
            generateRefreshToken(userId)
        )
    }

    private fun generateAccessToken(userId: UserId) = JWT.create()
        .withExpiresAt(clock.nowDate(days = accessTokenKeyExpirationDays))
        .withIssuedAt(clock.nowDate())
        .withPayload(setUpPayload(userId))
        .withIssuer(jwtIssuer)
        .sign(accessTokenAlgorithm)

    private fun generateRefreshToken(userId: UserId) = JWT.create()
        .withExpiresAt(clock.nowDate(days = refreshTokenKeyExpirationDays))
        .withIssuedAt(clock.nowDate())
        .withPayload(setUpPayload(userId))
        .withIssuer(jwtIssuer)
        .sign(refreshTokenAlgorithm)

    override fun validateAccessToken(token: String): String {
        val verifier: JWTVerifier = JWT
            .require(accessTokenAlgorithm)
            .withIssuer(jwtIssuer)
            .build()

        val jwt: DecodedJWT = verifier.verify(token)
        return jwt.subject
    }

    override fun validateRefreshToken(token: String): String {
        val verifier: JWTVerifier = JWT
            .require(refreshTokenAlgorithm)
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