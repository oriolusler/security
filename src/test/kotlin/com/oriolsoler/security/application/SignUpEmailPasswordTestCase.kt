package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.SignUpException
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.Verification
import com.oriolsoler.security.domain.email.ValidateEmailMailInformation
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserAlreadyExistsException
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mail.MailSender
import kotlin.test.assertEquals


class SignUpEmailPasswordTestCase {
    private val mailFrom = "info@business.com"

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
        given { signUpUserRepository.getBy(email) } willAnswer { throw UserNotFoundException() }

        val passwordService = mock<PasswordService> {
            on { encode(password) } doReturn encryptedPassword
        }

        val pin = "527832"
        val verification = Verification(verification = pin)
        val verifyService = mock<VerifyService> {
            on { generate() } doReturn verification
        }

        val mailSender = mock<MailSender> { }
        doNothing().`when`(mailSender).send(any())

        val emailInformation = ValidateEmailMailInformation(from = mailFrom, to = email, validation = pin)
        val emailService = mock<MailService> {
            on { send(emailInformation) } doReturn true
        }

        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        doNothing().`when`(verifyServiceRepository).save(any())

        val signUpEmailPasswordTestCase = SignUpEmailPasswordUseCase(
            signUpUserRepository,
            passwordService,
            verifyService,
            emailService,
            verifyServiceRepository,
            mailFrom
        )

        val signupRequestCommand = SignUpRequestCommand(email, password)
        val userCreated = signUpEmailPasswordTestCase.execute(signupRequestCommand)

        assertEquals(user.id, userCreated.id)
        assertEquals(user.password, userCreated.password)
        verify(passwordService, times(1)).encode(password)
        verify(signUpUserRepository, times(1)).save(any())
        verify(verifyService, times(1)).generate()
        verify(emailService, times(1)).send(emailInformation)
        verify(verifyServiceRepository, times(1)).save(any())
    }

    @Test
    fun `handle error if email is already used`() {
        val email = "user@email.com"
        val password = "password"
        val user = User(email = email, password = password)

        val userRepository = mock<UserRepository> {
            on { save(any()) } doReturn user
            on { getBy(email) } doReturn user
        }
        given { userRepository.checkIfUserAlreadyExists(email) } willAnswer { throw UserAlreadyExistsException() }

        val passwordService = mock<PasswordService> {}
        val verifyService = mock<VerifyService> {}
        val emailService = mock<MailService> {}
        val verifyServiceRepository = mock<VerifyServiceRepository> {}

        val signUpEmailPasswordTestCase = SignUpEmailPasswordUseCase(
            userRepository,
            passwordService,
            verifyService,
            emailService,
            verifyServiceRepository,
            mailFrom
        )

        val command = SignUpRequestCommand(email, password)
        val exception = assertThrows<SignUpException> { signUpEmailPasswordTestCase.execute(command) }
        assertEquals("SignUp error: User already exists", exception.message)

    }
}