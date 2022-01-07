package com.oriolsoler.security.application.signup

class SignUpException(message: String?, e: Exception) : Exception("SignUp error: $message", e)