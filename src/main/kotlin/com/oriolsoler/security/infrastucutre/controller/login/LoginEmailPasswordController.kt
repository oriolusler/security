package com.oriolsoler.security.infrastucutre.controller.login

import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginEmailPasswordController(
    private val loginEmailPasswordUseCase: LoginEmailPasswordUseCase
) {
    @PostMapping("/api/auth/login")
    fun register(@RequestBody loginRequestCommand: LoginRequestCommand): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .status(OK)
            .body(loginEmailPasswordUseCase.execute(loginRequestCommand))
    }

//    @ExceptionHandler([CustomException1::class, CustomException2::class])
//    fun handleException() {
//        //
//    }
}