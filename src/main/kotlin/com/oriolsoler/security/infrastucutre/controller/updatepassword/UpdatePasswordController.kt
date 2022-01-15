package com.oriolsoler.security.infrastucutre.controller.updatepassword

import com.oriolsoler.security.application.updatepassword.UpdatePasswordException
import com.oriolsoler.security.application.updatepassword.UpdatePasswordUseCase
import com.oriolsoler.security.domain.verification.VerificationNotVerifiedException
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UpdatePasswordController(private val updatePasswordUseCase: UpdatePasswordUseCase) {
    @PostMapping("/api/auth/password/update")
    fun update(@RequestBody updatePasswordRequestCommand: UpdatePasswordRequestCommand): ResponseEntity<String> {
        updatePasswordUseCase.execute(updatePasswordRequestCommand)
        return ResponseEntity
            .status(OK)
            .build()
    }

    @ExceptionHandler(UpdatePasswordException::class)
    fun handleUpdatePasswordError(error: UpdatePasswordException): ResponseEntity<String> {
        if (error.cause is UserNotFoundException || error.cause is VerificationNotFoundException) {
            return ResponseEntity
                .status(NOT_FOUND)
                .body(error.message)
        }

        if (error.cause is VerificationNotVerifiedException) {
            return ResponseEntity
                .status(UNAUTHORIZED)
                .body(error.message)
        }

        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(error.message)
    }
}