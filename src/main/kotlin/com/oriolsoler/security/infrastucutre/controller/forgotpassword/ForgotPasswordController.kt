package com.oriolsoler.security.infrastucutre.controller.forgotpassword

import com.oriolsoler.security.application.forgotpassword.ForgotPasswordUseCase
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ForgotPasswordController(private val forgotPasswordUseCase: ForgotPasswordUseCase) {
    @PostMapping("/api/auth/forgot")
    fun forgot(@RequestBody forgotPasswordCommand: ForgotPasswordRequestCommand): ResponseEntity<String> {
        forgotPasswordUseCase.execute(forgotPasswordCommand)
        return ResponseEntity
            .status(ACCEPTED)
            .build()
    }
}