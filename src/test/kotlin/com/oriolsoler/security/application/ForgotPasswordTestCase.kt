package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.forgotpassword.ForgotPasswordException
import com.oriolsoler.security.application.forgotpassword.ForgotPasswordUseCase
import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.Verification
import com.oriolsoler.security.domain.email.ForgotPasswordMailInformation
import com.oriolsoler.security.infrastucutre.controller.forgotpassword.ForgotPasswordRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ForgotPasswordTestCase {
    private val emailFrom = "email@online.com"

    @Test
    fun `should send forgot password`() {
        val userMail = "user@email.com"

        val pin = "527832"
        val verification = Verification(verification = pin)
        val verifyService = mock<VerifyService> {
            on { generate() } doReturn verification
        }

        val emailInformation = ForgotPasswordMailInformation(
            from = emailFrom,
            to = userMail,
            validation = pin
        )
        val emailService = mock<MailService> {
            on { send(emailInformation) } doReturn true
        }

        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        doNothing().`when`(verifyServiceRepository).save(any())

        val user = User(email = userMail)
        val userRepository = mock<UserRepository> {
            on { getBy(userMail) } doReturn user
        }

        val forgotPasswordTestCase = ForgotPasswordUseCase(
            verifyService,
            emailService,
            verifyServiceRepository,
            userRepository,
            emailFrom
        )

        val forgotPasswordRequestCommand = ForgotPasswordRequestCommand(userMail)

        forgotPasswordTestCase.execute(forgotPasswordRequestCommand)

        assertEquals("Este es su código para cambiar la contraseña: $pin", emailInformation.subject)
        assertEquals(
            "Has solicitado el cambio de la contraseña, " +
                    "introduce este código para seguir con el proceso: $pin", emailInformation.body
        )
        verify(userRepository, times(1)).getBy(userMail)
        verify(verifyService, times(1)).generate()
        verify(emailService, times(1)).send(emailInformation)
        verify(verifyServiceRepository, times(1)).save(any())
    }

    @Test
    fun `should throw error if user not found`() {
        val userMail = "user@email.com"

        val verifyService = mock<VerifyService> {}
        val emailService = mock<MailService> { }
        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        val userRepository = mock<UserRepository> { }
        given { userRepository.getBy(userMail) } willAnswer { throw UserNotFoundException() }

        val forgotPasswordTestCase = ForgotPasswordUseCase(
            verifyService,
            emailService,
            verifyServiceRepository,
            userRepository,
            emailFrom
        )

        val forgotPasswordRequestCommand = ForgotPasswordRequestCommand(userMail)
        val exception = assertThrows<ForgotPasswordException> {
            forgotPasswordTestCase.execute(forgotPasswordRequestCommand)
        }
        assertEquals("Forgot password error: User not found", exception.message)

    }
}