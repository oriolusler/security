package com.oriolsoler.security.application.validaterefreshtoken

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.application.accessverification.TokenVerification
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.domain.Token
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.services.exceptions.TokenValidationException
import com.oriolsoler.security.domain.user.UserId
import com.oriolsoler.security.domain.user.UserLockedException
import com.oriolsoler.security.infrastucutre.controller.validaterefreshtoken.ValidateRefreshTokenCommand

class ValidateRefreshTokenUseCase(
    private val tokenVerification: TokenVerification,
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator
) {
    fun execute(validateRefreshTokenCommand: ValidateRefreshTokenCommand): Token {
        val currentUserUUID = isValidVerification(validateRefreshTokenCommand.refreshToken)
        val user = userRepository.getBy(currentUserUUID)
        isValidUser(user)
        return tokenGenerator.generate(user.id)
    }

    private fun isValidVerification(refreshToken: String): UserId {
        try {
            return UserId(tokenVerification.validateRefreshToken(refreshToken))
        } catch (e: TokenValidationException) {
            throw ValidateRefreshTokenException(e.message, e)
        }
    }

    private fun isValidUser(user: User) {
        try {
            user.isValid()
        } catch (e: UserLockedException) {
            throw ValidateRefreshTokenException(e.message, e)
        }
    }
}