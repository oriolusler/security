package com.oriolsoler.security.application.validateverification

import com.oriolsoler.security.domain.verification.VerificationException

class VerifyException(message: String?, e: VerificationException) : Exception("Verification error: $message", e)