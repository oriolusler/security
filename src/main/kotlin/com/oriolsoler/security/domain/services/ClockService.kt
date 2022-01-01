package com.oriolsoler.security.domain.services

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class ClockService {
    fun now(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun nowDate(days: Long = 0): Date {
        return localDateTimeToDate(now().plusDays(days))
    }

    companion object {
        fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        }
    }
}