package com.oriolsoler.security.infrastucutre.controller.verifyVerification

import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
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
}