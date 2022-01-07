package com.oriolsoler.security.domain.services

import com.oriolsoler.security.application.PasswordService
import com.oriolsoler.security.domain.services.exceptions.InvalidPasswordException
import org.springframework.security.crypto.password.PasswordEncoder

class SpringEncoderPasswordService(private val passwordEncoder: PasswordEncoder) : PasswordService {
    override fun matches(rawPassword: String, encryptedPassword: String): Boolean {
        return if (passwordEncoder.matches(rawPassword, encryptedPassword)) {
            true
        } else {
            throw InvalidPasswordException()
        }
    }

    override fun encode(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
    }
}