package com.oriolsoler.security.application.forgotpassword

class UpdatePasswordException(message: String?, cause: Throwable?) :
    Exception("Update password error: $message", cause)