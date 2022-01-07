package com.oriolsoler.security.infrastucutre.controller.accessverification

import com.oriolsoler.security.application.accessverification.AccessVerificationException
import com.oriolsoler.security.application.accessverification.AccessVerificationUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AccessVerificationController(
    private val accessVerificationUseCase: AccessVerificationUseCase
) {
    @GetMapping("/api/auth/validate")
    fun validate(@RequestHeader("Authorization") authorization: String): ResponseEntity<Void> {
        if (accessVerificationUseCase.execute(AccessVerificationCommand(authorization))) {
            return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build()
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    @ExceptionHandler(AccessVerificationException::class)
    fun handleAccessVerificationException(e: AccessVerificationException) = ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(e.message)
}