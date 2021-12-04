package com.oriolsoler.security.domain.user

import java.util.UUID

class UserId(val value: UUID = UUID.randomUUID()) {
    constructor(value: String) : this(UUID.fromString(value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserId

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}