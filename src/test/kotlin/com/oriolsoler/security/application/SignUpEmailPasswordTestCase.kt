package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.signup.*
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserRepositoryError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

        val signUpUserRepository = mock<UserRepository> {
            on { save(any()) } doReturn user
        }
        given { signUpUserRepository.getBy(email) } willAnswer { throw UserRepositoryError("User not found") }

        val passwordEncoder = mock<PasswordEncoder> {
            on { encode(password) } doReturn encryptedPassword
        }

        val pin = "527832"
        val verification = Verification(verification = pin)
        val verifyService = mock<VerifyService> {
            on { generate() } doReturn verification
        }

        val emailService = mock<EmailService> {
            on { send(email, pin) } doReturn true
        }

        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        doNothing().`when`(verifyServiceRepository).save(any())

        val signUpEmailPasswordTestCase = SignUpEmailPasswordUseCase(
            signUpUserRepository,
            passwordEncoder,
            verifyService,
            emailService,
            verifyServiceRepository
        )

        val signupRequestCommand = SignUpRequestCommand(email, password)
        val userCreated = signUpEmailPasswordTestCase.execute(signupRequestCommand)

        assertEquals(user.id, userCreated.id)
        assertEquals(user.password, userCreated.password)
        verify(passwordEncoder, times(1)).encode(password)
        verify(signUpUserRepository, times(1)).save(any())
        verify(verifyService, times(1)).generate()
        verify(emailService, times(1)).send(email, pin)
        verify(verifyServiceRepository, times(1)).save(any())
    }

    @Test
    fun `handle error if email is already used`() {
        val email = "user@email.com"
        val password = "password"
        val user = User(email = email, password = password)

        val signUpUserRepository = mock<UserRepository> {
            on { save(any()) } doReturn user
            on { getBy(email) } doReturn user
        }

        val passwordEncoder = mock<PasswordEncoder> {}
        val verifyService = mock<VerifyService> {}
        val emailService = mock<EmailService> {}
        val verifyServiceRepository = mock<VerifyServiceRepository> {}

        val signUpEmailPasswordTestCase = SignUpEmailPasswordUseCase(
            signUpUserRepository,
            passwordEncoder,
            verifyService,
            emailService,
            verifyServiceRepository
        )

        val command = SignUpRequestCommand(email, password)
        val exception = assertThrows<SignUpException> { signUpEmailPasswordTestCase.execute(command) }
        assertEquals("SignUp error: Email already used", exception.message)

    }
}