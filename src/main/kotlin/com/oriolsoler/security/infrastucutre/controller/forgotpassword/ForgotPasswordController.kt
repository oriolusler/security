package com.oriolsoler.security.infrastucutre.controller.forgotpassword

import com.oriolsoler.security.application.forgotpassword.ForgotPasswordException
import com.oriolsoler.security.application.forgotpassword.ForgotPasswordUseCase
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
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

    @ExceptionHandler(ForgotPasswordException::class)
    fun handleForgotPasswordError(error: ForgotPasswordException): ResponseEntity<String> {
        if (error.cause is UserNotFoundException) {
            return ResponseEntity
                .status(NOT_FOUND)
                .body(error.message)
        }
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(error.message)
    }
}