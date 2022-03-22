package com.oriolsoler.security.infrastucutre.controller.login.firebase

import com.oriolsoler.security.application.login.firebase.LoginFirebaseUseCase
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginFirebaseController(private val loginFirebaseUseCase: LoginFirebaseUseCase) {
    @PostMapping("/api/auth/login/firebase")
    fun login(@RequestBody loginFirebaseRequestCommand: LoginFirebaseRequestCommand): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(loginFirebaseUseCase.execute(loginFirebaseRequestCommand))
    }
}