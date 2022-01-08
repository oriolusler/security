package com.oriolsoler.security.domain.services

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.email.MailInformation
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.springframework.mail.MailSendException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class MailSenderServiceTest {
    private val emailSender = "email@online.com"
    private val bodyText = "Hello, this is just a dummy mail body"
    private val subjectText = "Dummy mail subject"
    private val user = User(email = "email@online.com")

    private val mailInformation = MailInformation(
        from = emailSender,
        to = user.email,
        subject = subjectText,
        body = bodyText
    )

    @Test
    fun `should send an email`() {
        val javaMailSenderMock = mock<JavaMailSender> { }
        doNothing().`when`(javaMailSenderMock).send(any<SimpleMailMessage>())
        val captor = ArgumentCaptor.forClass(SimpleMailMessage::class.java)

        val mailSenderService = MailSenderService(javaMailSenderMock)

        val messageHasBeenSent = mailSenderService.send(mailInformation)

        assertTrue { messageHasBeenSent }
        verify(javaMailSenderMock, times(1)).send(captor.capture())

        val messageSent = captor.value as SimpleMailMessage
        assertEquals(user.email, messageSent.to!![0])
        assertEquals(emailSender, messageSent.from)
        assertEquals(subjectText, messageSent.subject)
        assertEquals(bodyText, messageSent.text)
    }

    @Test
    fun `return false if exception occurred`() {
        val javaMailSenderMock = mock<JavaMailSender> {}
        `when`(javaMailSenderMock.send(any<SimpleMailMessage>())).thenThrow(MailSendException::class.java)

        val mailSenderService = MailSenderService(javaMailSenderMock)

        val messageSent = mailSenderService.send(mailInformation)

        assertFalse { messageSent }
    }
}