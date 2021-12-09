package com.oriolsoler.security.domain.services

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.domain.User
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.springframework.mail.MailSendException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class VerificationEmailServiceTest {
    private val emailSender = "email@online.com"

    @Test
    fun `send email to verify a pin given a email`() {
        val user = User(email = "email@online.com")
        val message = "72823"

        val javaMailSenderMock = mock<JavaMailSender> { }
        doNothing().`when`(javaMailSenderMock).send(any<SimpleMailMessage>())
        val captor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)

        val verificationEmailService = VerificationEmailService(javaMailSenderMock, emailSender)

        val messageHasBeenSent = verificationEmailService.send(user.email, message)

        assertTrue { messageHasBeenSent }
        verify(javaMailSenderMock, times(1)).send(captor.capture())
        val messageSent = captor.value as SimpleMailMessage
        assertEquals(user.email, messageSent.to!![0])
        assertEquals(emailSender, messageSent.from)
        assertEquals("Este es su código de confirmación: 72823", messageSent.subject)
    }

    @Test
    fun `return false if exception occurred`() {
        val user = User(email = "email@online.com")
        val message = "72823"

        val javaMailSenderMock = mock<JavaMailSender> {}
        `when`(javaMailSenderMock.send(any<SimpleMailMessage>())).thenThrow(MailSendException::class.java)

        val verificationEmailService = VerificationEmailService(javaMailSenderMock, emailSender)

        val messageSent = verificationEmailService.send(user.email, message)

        assertFalse { messageSent }
    }
}