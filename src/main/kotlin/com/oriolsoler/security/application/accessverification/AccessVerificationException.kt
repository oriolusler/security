package com.oriolsoler.security.application.accessverification

class AccessVerificationException(message: String?, e: Exception) :
    Exception("Access verification error: $message", e)