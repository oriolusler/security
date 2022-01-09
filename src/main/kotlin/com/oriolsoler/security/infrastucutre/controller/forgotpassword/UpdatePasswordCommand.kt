package com.oriolsoler.security.infrastucutre.controller.forgotpassword

class UpdatePasswordCommand(val mail: String, val verification: String, val newPassword: String)