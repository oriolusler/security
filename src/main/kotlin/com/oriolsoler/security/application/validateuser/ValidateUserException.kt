package com.oriolsoler.security.application.validateuser

class ValidateUserException(message: String?, e: Exception? = null) : Exception("Validate user error: $message", e)