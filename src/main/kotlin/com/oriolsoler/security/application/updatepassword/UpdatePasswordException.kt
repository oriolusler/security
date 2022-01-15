package com.oriolsoler.security.application.updatepassword

class UpdatePasswordException(message: String?, cause: Throwable?) :
    Exception("Update password error: $message", cause)