package com.oriolsoler.security.infrastucutre.controller.validateuser

import com.oriolsoler.security.application.validateuser.ValidateUserException
import com.oriolsoler.security.application.validateuser.ValidateUserUseCase
import com.oriolsoler.security.domain.verification.VerificationExpiredException
import com.oriolsoler.security.domain.verification.VerificationUsedException
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
class ValidateUserController(
    private val validateUserUseCase: ValidateUserUseCase
) {
    @PostMapping("/api/auth/validate/user")
    fun verify(@RequestBody verificationCommand: ValidateUserCommand): ResponseEntity<Unit> {
        return ResponseEntity
            .status(ACCEPTED)
            .body(validateUserUseCase.execute(verificationCommand))
    }

    @ExceptionHandler(ValidateUserException::class)
    fun handleVerificationError(error: ValidateUserException): ResponseEntity<String> {
        if (error.cause is VerificationExpiredException) return ResponseEntity
            .status(GONE)
            .body(error.message)

        if (error.cause is VerificationUsedException) return ResponseEntity
            .status(CONFLICT)
            .body(error.message)

        if (error.cause is VerificationNotFoundException || error.cause is UserNotFoundException) return ResponseEntity
            .status(NOT_FOUND)
            .body(error.message)

        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(error.message)
    }
}