package com.oriolsoler.security.domain

import com.oriolsoler.security.domain.email.MailInformation
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MailInformationTest {
    private val from = "from@email.com"
    private val to = "to@email.com"
    private val subject = "Dummy subject"
    private val body = "This is a dummy body text"

    @Test
    fun `if mail information object has the same data should be de same`() {
        val mailInformation1 = MailInformation(from, to, subject, body)
        val mailInformation2 = MailInformation(from, to, subject, body)

        assertEquals(mailInformation1, mailInformation2)
    }

    @Test
    fun `mail information must have unique hash`() {
        val mailInformation1 = MailInformation(from, to, subject, body)
        assertNotNull(mailInformation1.hashCode())
    }
}