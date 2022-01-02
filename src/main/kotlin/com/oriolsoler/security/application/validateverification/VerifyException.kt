package com.oriolsoler.security.application.validateverification

class VerifyException(message: String?, e: Exception) : Exception("Verification error: $message", e)