package com.oriolsoler.security.domain.services.exceptions

class TokenValidationException(message: String?) : Exception("Invalid token: $message")