package com.oriolsoler.security.application

interface PasswordService {
    fun matches(rawPassword: String, encryptedPassword: String): Boolean
    fun encode(rawPassword: String): String
}