package com.oriolsoler.security.application.resenduserverification

class ResendUserVerificationException(message: String?, e: Exception) :
    Exception("Resend user verification error: $message", e)