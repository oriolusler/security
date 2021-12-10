package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.signup.EmailService
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.application.signup.VerifyService
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals


class SignUpEmailPasswordTestCase {

    @Test
    fun `sign up correctly with email and password`() {
        val email = "user@email.com"
        val password = "password"
        val encryptedPassword = "encrypted_password"
        val roles = listOf(ROLE_USER)

        val user = User(email = email, password = encryptedPassword, roles = roles)

        val signUpUserRepository = mock<SignUpUserRepository> {
            on { save(any()) } doReturn user
        }

        val passwordEncoder = mock<PasswordEncoder> {
            on { encode(password) } doReturn encryptedPassword
        }

        val pin = "527832"
        val verifyService = mock<VerifyService> {
            on { generate() } doReturn pin
        }

        val emailService = mock<EmailService> {
            on { send(email, pin) } doReturn true
        }

        val signUpEmailPasswordTestCase = SignUpEmailPasswordUseCase(
            signUpUserRepository,
            passwordEncoder,
            verifyService,
            emailService
        )

        val signupRequestCommand = SignUpRequestCommand(email, password)
        val userCreated = signUpEmailPasswordTestCase.execute(signupRequestCommand)

        assertEquals(user.id, userCreated.id)
        assertEquals(user.password, userCreated.password)
        verify(passwordEncoder, times(1)).encode(password)
        verify(signUpUserRepository, times(1)).save(any())
        verify(verifyService, times(1)).generate()
        verify(emailService, times(1)).send(email, pin)
    }
}