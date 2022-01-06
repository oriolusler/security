package com.oriolsoler.security.domain.email

open class MailInformation(val from: String, val to: String, val subject: String, val body: String){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MailInformation) return false

        if (from != other.from) return false
        if (to != other.to) return false
        if (subject != other.subject) return false
        if (body != other.body) return false

        return true
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + body.hashCode()
        return result
    }
}