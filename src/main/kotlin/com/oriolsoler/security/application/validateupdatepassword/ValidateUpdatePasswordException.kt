package com.oriolsoler.security.application.validateupdatepassword

class ValidateUpdatePasswordException(message: String?, e: Exception) :
    Exception("Validate update password error: $message", e)