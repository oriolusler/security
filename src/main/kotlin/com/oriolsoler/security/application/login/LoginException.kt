package com.oriolsoler.security.application.login

class LoginException(message: String?, e: Exception) : Exception("Login error: $message", e)