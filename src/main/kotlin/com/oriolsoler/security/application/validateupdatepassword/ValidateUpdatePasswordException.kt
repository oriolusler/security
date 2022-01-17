package com.oriolsoler.security.application.validateupdatepassword

class ValidateUpdatePasswordException(message: String?, e: Exception? = null) :
    Exception("Validate update password error: $message", e)