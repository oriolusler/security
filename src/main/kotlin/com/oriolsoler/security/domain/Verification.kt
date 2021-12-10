package com.oriolsoler.security.domain

import java.time.LocalDateTime

data class Verification(
    val verification: String,
    val creationDate: LocalDateTime = LocalDateTime.now(),
    val expirationDate: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    val used: Boolean = false
)

data class UserVerification(
    val user: User,
    val verification: Verification
)