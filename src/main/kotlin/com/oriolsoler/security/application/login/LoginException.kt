package com.oriolsoler.security.application.login

class LoginException(message: String?) : Exception("Login error: $message")