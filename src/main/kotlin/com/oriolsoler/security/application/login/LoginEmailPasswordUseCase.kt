package com.oriolsoler.security.application.login

import com.oriolsoler.security.infrastucutre.controller.login.LoginRequestCommand
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import org.springframework.security.crypto.password.PasswordEncoder

class LoginEmailPasswordUseCase(
    private val loginUserRepository: LoginUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGenerator: TokenGenerator
) {
    fun execute(loginRequestCommand: LoginRequestCommand): LoginResponse {
        val currentUser = loginUserRepository.getBy(loginRequestCommand.email)
        validPassword(loginRequestCommand.password, currentUser.password!!)
        val token = tokenGenerator.generate(currentUser)
        return LoginResponse(
            token.value,
            token.type,
            currentUser.id,
            currentUser.name,
            currentUser.email,
            currentUser.roles
        )
    }

    private fun validPassword(rawPassword: String, encodedPassword: String) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw RuntimeException("Invalid username or password");
        }
    }
}