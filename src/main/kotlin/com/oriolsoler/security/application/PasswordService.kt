package com.oriolsoler.security.application

interface PasswordService {
    fun matches(rawPassword: String, encryptedPassword: String)
    fun encode(rawPassword: String): String
}