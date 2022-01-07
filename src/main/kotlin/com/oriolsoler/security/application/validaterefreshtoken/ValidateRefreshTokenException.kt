package com.oriolsoler.security.application.validaterefreshtoken

class ValidateRefreshTokenException(message: String?, e: Exception) :
    Exception("Refresh token validation error: $message", e)