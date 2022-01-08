package com.oriolsoler.security.domain.services

import com.oriolsoler.security.application.signup.MailService
import com.oriolsoler.security.domain.email.MailInformation
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

class MailSenderService(private val emailSender: JavaMailSender) : MailService {
    override fun send(mailInformation: MailInformation): Boolean {
        return try {
            val mailToSend = SimpleMailMessage()
            mailToSend.setFrom(mailInformation.from)
            mailToSend.setTo(mailInformation.to)
            mailToSend.setSubject(mailInformation.subject)
            mailToSend.setText(mailInformation.body)
            emailSender.send(mailToSend)
            true
        } catch (exception: MailException) {
            false
        }
    }
}