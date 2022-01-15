package com.oriolsoler.security.application.validateuser

class ValidateUserException(message: String?, e: Exception) : Exception("Validate user error: $message", e)