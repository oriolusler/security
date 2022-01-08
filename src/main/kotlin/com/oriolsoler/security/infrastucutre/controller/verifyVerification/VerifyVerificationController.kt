package com.oriolsoler.security.infrastucutre.controller.verifyVerification

import com.oriolsoler.security.application.validateverification.VerifyException
import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import com.oriolsoler.security.domain.verification.VerificationExpiredException
import com.oriolsoler.security.domain.verification.VerificationUsedException
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VerifyVerificationController(
    private val verifyVerificationUseCase: VerifyVerificationUseCase
) {
    @PostMapping("/api/auth/verify")
    fun verify(@RequestBody verificationCommand: VerifyVerificationCommand): ResponseEntity<Unit> {
        return ResponseEntity
            .status(ACCEPTED)
            .body(verifyVerificationUseCase.execute(verificationCommand))
    }

    @ExceptionHandler(VerifyException::class)
    fun handleVerificationError(error: VerifyException): ResponseEntity<String> {
        if (error.cause is VerificationExpiredException) return ResponseEntity
            .status(GONE)
            .body(error.message)

        if (error.cause is VerificationUsedException) return ResponseEntity
            .status(CONFLICT)
            .body(error.message)

        if (error.cause is VerificationNotFoundException || error.cause is UserNotFoundException) return ResponseEntity
            .status(NOT_FOUND)
            .body(error.message)

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error.message)
    }
}