package com.oriolsoler.security.infrastucutre.controller.resenduservalidation

import com.oriolsoler.security.application.resenduserverification.ResendUserVerificationUseCase
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ResendUserValidationController(
    private val resendUserValidationUseCase: ResendUserVerificationUseCase
) {
    @PostMapping("/api/auth/validate/user/resend")
    fun resend(@RequestBody resendUserValidationCommand: ResendUserValidationCommand): ResponseEntity<Void> {
        resendUserValidationUseCase.execute(resendUserValidationCommand)
        return ResponseEntity
            .status(ACCEPTED)
            .build()
    }
}