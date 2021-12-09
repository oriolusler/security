package com.oriolsoler.security.domain.services

import com.oriolsoler.security.application.signup.EmailService
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender


class VerificationEmailService(
    private val emailSender: JavaMailSender,
    private val emailFrom: String
) : EmailService {
    private val verificationMessage = "Para confirmar tu correo, simplemente vuelve a la ventana del explorador" +
            " en la que comenzaste a crear tu cuenta de NeverEatAlone e introduce este código: %s"
    private val verificationMessageSubject = "Este es su código de confirmación: %s"

    override fun send(email: String, message: String): Boolean {
        return try {
            val mailToSend = SimpleMailMessage()
            mailToSend.setFrom(emailFrom)
            mailToSend.setTo(email)
            mailToSend.setSubject(String.format(verificationMessageSubject, message))
            mailToSend.setText(String.format(verificationMessage, message))
            emailSender.send(mailToSend)
            true
        } catch (exception: MailException) {
            false
        }
    }
}