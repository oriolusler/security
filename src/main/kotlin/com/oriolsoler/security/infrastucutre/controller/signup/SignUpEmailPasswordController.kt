package com.oriolsoler.security.infrastucutre.controller.signup

import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SignUpEmailPasswordController(
    private val signUpEmailPasswordUseCase: SignUpEmailPasswordUseCase
) {
    @PostMapping("/api/auth/register")
    fun register(signupRequestCommand: SignUpRequestCommand): ResponseEntity<String> {
        return ResponseEntity
            .status(OK)
            .body(SignUpResponse(signUpEmailPasswordUseCase.execute(signupRequestCommand)).response())
    }
}