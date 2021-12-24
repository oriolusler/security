package com.oriolsoler.security.application.accessverification

class AccessVerificationException(message: String?) : Exception("Access verification error: $message")