package com.oriolsoler.security.infrastucutre.controller.accessverification

class AccessVerificationCommand(val accessToken: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccessVerificationCommand

        if (accessToken != other.accessToken) return false

        return true
    }

    override fun hashCode(): Int {
        return accessToken.hashCode()
    }
}