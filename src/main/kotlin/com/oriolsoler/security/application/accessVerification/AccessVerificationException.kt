package com.oriolsoler.security.application.accessVerification

class AccessVerificationException(message: String?) : Exception("Access verification error: $message")