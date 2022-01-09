package com.oriolsoler.security.application.validateverification

class VerifyException(message: String?, e: Exception) : Exception("Verify error: $message", e)