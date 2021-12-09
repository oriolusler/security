package com.oriolsoler.security.application.signup

interface EmailService {
    fun send(email: String, message: String): Boolean
}