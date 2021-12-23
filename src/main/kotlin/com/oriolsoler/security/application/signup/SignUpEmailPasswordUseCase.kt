package com.oriolsoler.security.application.signup

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserRepositoryError
import org.springframework.security.crypto.password.PasswordEncoder

class SignUpEmailPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val verifyService: VerifyService,
    private val emailService: EmailService,
    private val verifyServiceRepository: VerifyServiceRepository
) {
    fun execute(signupRequestCommand: SignUpRequestCommand): User {
        val userCreated = saveNewUser(signupRequestCommand)
        val userVerification = UserVerification(userCreated, verifyService.generate())
        verifyServiceRepository.save(userVerification)
        emailService.send(userVerification.user.email, userVerification.verification.verification)
        return userCreated
    }

    private fun saveNewUser(signupRequestCommand: SignUpRequestCommand): User {
        if (userAlreadyExists(signupRequestCommand.email)) {
            throw SignUpException("Email already used")
        }
        return userRepository.save(
            User(
                email = signupRequestCommand.email,
                password = passwordEncoder.encode(signupRequestCommand.password)
            )
        )
    }

    private fun userAlreadyExists(email: String): Boolean {
        return try {
            userRepository.getBy(email)
            true
        } catch (e: UserRepositoryError) {
            false
        }
    }
}
