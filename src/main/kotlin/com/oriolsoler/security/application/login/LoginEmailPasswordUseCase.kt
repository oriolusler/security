package com.oriolsoler.security.application.login

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import org.springframework.security.crypto.password.PasswordEncoder

class LoginEmailPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGenerator: TokenGenerator
) {
    fun execute(loginRequestCommand: LoginRequestCommand): LoginResponse {
        val currentUser = userRepository.getBy(loginRequestCommand.email)

        isValidUser(currentUser)
        isValidPassword(loginRequestCommand.password, currentUser.password)

        val token = tokenGenerator.generate(currentUser.id)
        return LoginResponse(token.value, token.type, currentUser.id, currentUser.email)
    }

    private fun isValidUser(user: User) {
        if (user.locked) {
            throw RuntimeException("User locked")
        }
    }

    private fun isValidPassword(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw RuntimeException("Invalid password")
        }
    }
}