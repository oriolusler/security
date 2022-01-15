package com.oriolsoler.security.infrastucutre.controller.validateupdatepassword

import com.oriolsoler.security.application.validateupdatepassword.ValidateUpdatePasswordException
import com.oriolsoler.security.application.validateupdatepassword.ValidateUpdatePasswordUseCase
import com.oriolsoler.security.domain.verification.VerificationExpiredException
import com.oriolsoler.security.domain.verification.VerificationAlreadyVerifiedException
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.GONE
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ValidateUpdatePasswordController(
    private val validateUpdatePasswordUseCase: ValidateUpdatePasswordUseCase
) {
    @PostMapping("/api/auth/validate/update-password")
    fun verify(@RequestBody verificationCommand: ValidateUpdatedPasswordCommand): ResponseEntity<Unit> {
        return ResponseEntity
            .status(ACCEPTED)
            .body(validateUpdatePasswordUseCase.execute(verificationCommand))
    }

    @ExceptionHandler(ValidateUpdatePasswordException::class)
    fun handleVerificationError(error: ValidateUpdatePasswordException): ResponseEntity<String> {
        if (error.cause is VerificationExpiredException) return ResponseEntity
            .status(GONE)
            .body(error.message)

        if (error.cause is VerificationAlreadyVerifiedException) return ResponseEntity
            .status(CONFLICT)
            .body(error.message)

        if (error.cause is VerificationNotFoundException || error.cause is UserNotFoundException) return ResponseEntity
            .status(NOT_FOUND)
            .body(error.message)

        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(error.message)
    }
}