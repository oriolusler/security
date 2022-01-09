package com.oriolsoler.security.infrastucutre.controller.forgotpassword

class UpdatePasswordRequestCommand(val email: String, val verification: String, val newPassword: String)