package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.resenduserverification.ResendUserVerificationException
import com.oriolsoler.security.application.resenduserverification.ResendUserVerificationUseCase
import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.domain.email.ValidateEmailMailInformation
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.Verification
import com.oriolsoler.security.domain.verification.VerificationType.VALIDATE_USER
import com.oriolsoler.security.infrastucutre.controller.resenduservalidation.ResendUserValidationCommand
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ResendUserVerificationTestCase {
    private val emailFrom = "email@online.com"

    @Test
    fun `should send verification`() {
        val userMail = "user@email.com"
        val pin = "527832"

        val user = User(email = userMail)
        val userRepository = mock<UserRepository> {
            on { getBy(userMail) } doReturn user
        }

        val verification = Verification(verification = pin, type = VALIDATE_USER)
        val verifyService = mock<VerifyService> {
            on { generate(VALIDATE_USER) } doReturn verification
        }

        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        doNothing().`when`(verifyServiceRepository).save(any())

        val emailInformation = ValidateEmailMailInformation(from = emailFrom, to = userMail, validation = pin)
        val emailService = mock<MailService> {
            on { send(emailInformation) } doReturn true
        }

        val resendUserVerificationUseCase = ResendUserVerificationUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            emailService,
            emailFrom
        )

        val resendUserVerificationCommand = ResendUserValidationCommand(userMail)

        resendUserVerificationUseCase.execute(resendUserVerificationCommand)

        assertEquals("Este es su código de confirmación: $pin", emailInformation.subject)
        assertEquals(
            "Para confirmar tu correo, simplemente vuelve a la ventana del explorador en la que" +
                    " comenzaste a crear tu cuenta de NeverEatAlone e introduce este código: $pin",
            emailInformation.body
        )

        verify(userRepository).getBy(userMail)
        verify(verifyService).generate(VALIDATE_USER)
        verify(emailService).send(emailInformation)
        verify(verifyServiceRepository).save(any())
    }

    @Test
    fun `should throw error if user already validated`() {
        val userMail = "user@email.com"

        val user = User(email = userMail, locked = false)
        val userRepository = mock<UserRepository> {
            on { getBy(userMail) } doReturn user
        }

        val verifyService = mock<VerifyService> {}
        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        val emailService = mock<MailService> {}

        val resendUserVerificationUseCase = ResendUserVerificationUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            emailService,
            emailFrom
        )

        val resendUserVerificationCommand = ResendUserValidationCommand(userMail)

        val exception = assertThrows<ResendUserVerificationException> {
            resendUserVerificationUseCase.execute(resendUserVerificationCommand)
        }

        assertEquals("Resend user verification error: User already validated", exception.message)
        verify(userRepository).getBy(userMail)
        verify(verifyService, never()).generate(any())
        verify(emailService, never()).send(any())
        verify(verifyServiceRepository, never()).save(any())
    }
}