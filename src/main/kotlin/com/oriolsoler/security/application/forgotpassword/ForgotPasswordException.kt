package com.oriolsoler.security.application.forgotpassword

class ForgotPasswordException(message: String?, cause: Throwable?) : Exception("Forgot password error: $message", cause)