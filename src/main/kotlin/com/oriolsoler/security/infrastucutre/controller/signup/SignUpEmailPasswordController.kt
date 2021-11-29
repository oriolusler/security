package com.oriolsoler.security.infrastucutre.controller.signup

import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SignUpEmailPasswordController(
    private val signUpEmailPasswordUseCase: SignUpEmailPasswordUseCase
) {
    @PostMapping("/api/auth/register")
    fun register(@RequestBody signupRequestCommand: SignUpRequestCommand): ResponseEntity<String> {
        return ResponseEntity
            .status(CREATED)
            .body(SignUpResponse(signUpEmailPasswordUseCase.execute(signupRequestCommand)).response())
    }
}