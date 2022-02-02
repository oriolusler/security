package com.oriolsoler.security.application.login

import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.services.exceptions.InvalidPasswordException
import com.oriolsoler.security.domain.user.UserLockedException
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import com.oriolsoler.security.infrastucutre.controller.login.ResponseUser
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException

class LoginEmailPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val tokenGenerator: TokenGenerator
) {
    fun execute(loginRequestCommand: LoginRequestCommand): LoginResponse {
        val currentUser = getUserByEmail(loginRequestCommand.email)

        validateUser(currentUser)
        validatePassword(loginRequestCommand.password, currentUser.password)

        val token = tokenGenerator.generate(currentUser.id)
        return LoginResponse(
            token = token,
            user = ResponseUser(currentUser.id.value.toString(), currentUser.email)
        )
    }

    private fun getUserByEmail(email: String): User {
        return try {
            userRepository.getBy(email)
        } catch (e: UserNotFoundException) {
            throw LoginException(e.message, e)
        }
    }

    private fun validateUser(user: User) {
        try {
            user.checkIfValid()
        } catch (e: UserLockedException) {
            throw LoginException(e.message, e)
        }
    }

    private fun validatePassword(rawPassword: String, encodedPassword: String) {
        try {
            passwordService.matches(rawPassword, encodedPassword)
        } catch (e: InvalidPasswordException) {
            throw LoginException("Invalid password", e)
        }
    }
}