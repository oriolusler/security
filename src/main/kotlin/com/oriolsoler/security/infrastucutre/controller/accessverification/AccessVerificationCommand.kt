package com.oriolsoler.security.infrastucutre.controller.accessverification

class AccessVerificationCommand(val token: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccessVerificationCommand

        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        return token.hashCode()
    }
}