package com.oriolsoler.security.application.validaterefreshtoken

class ValidateRefreshTokenException(message: String?) : Exception("Refresh token validation error: $message")