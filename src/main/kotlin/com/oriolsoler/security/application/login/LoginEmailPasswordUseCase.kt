package com.oriolsoler.security.application.login

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserException
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import com.oriolsoler.security.infrastucutre.repository.user.UserRepositoryError
import org.springframework.security.crypto.password.PasswordEncoder

class LoginEmailPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGenerator: TokenGenerator
) {
    fun execute(loginRequestCommand: LoginRequestCommand): LoginResponse {
        val currentUser = getUserByEmail(loginRequestCommand.email)

        isValidUser(currentUser)
        isValidPassword(loginRequestCommand.password, currentUser.password)

        val token = tokenGenerator.generate(currentUser.id, 7)
        return LoginResponse(token.value, token.type, currentUser.id, currentUser.email)
    }

    private fun getUserByEmail(email: String): User {
        return try {
            userRepository.getBy(email)
        } catch (e: UserRepositoryError) {
            throw LoginException(e.message)
        }
    }

    private fun isValidUser(user: User) {
        try {
            user.isValid()
        } catch (e: UserException) {
            throw LoginException(e.message)
        }
    }

    private fun isValidPassword(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw LoginException("Invalid password")
        }
    }
}