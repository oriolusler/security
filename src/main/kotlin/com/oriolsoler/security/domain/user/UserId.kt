package com.oriolsoler.security.domain.user

import java.util.UUID

class UserId(val value: UUID = UUID.randomUUID()) {
    constructor(value: String) : this(UUID.fromString(value))
}