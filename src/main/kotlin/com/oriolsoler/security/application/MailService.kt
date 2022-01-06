package com.oriolsoler.security.application.signup

import com.oriolsoler.security.domain.email.MailInformation

interface MailService {
    fun send(mailInformation: MailInformation): Boolean
}