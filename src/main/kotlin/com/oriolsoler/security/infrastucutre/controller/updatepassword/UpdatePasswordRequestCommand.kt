package com.oriolsoler.security.infrastucutre.controller.updatepassword

class UpdatePasswordRequestCommand(val email: String, val verification: String, val newPassword: String)