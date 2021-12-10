package com.oriolsoler.security.domain.services

import java.time.LocalDateTime

class ClockService {
    fun now(): LocalDateTime {
        return LocalDateTime.now()
    }
}