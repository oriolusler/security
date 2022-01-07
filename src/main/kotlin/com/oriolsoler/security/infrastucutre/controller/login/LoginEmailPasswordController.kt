package com.oriolsoler.security.infrastucutre.controller.login

import com.oriolsoler.security.application.login.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.LoginException
import com.oriolsoler.security.domain.user.UserLockedException
import org.springframework.http.HttpStatus.*
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
    fun login(@RequestBody loginRequestCommand: LoginRequestCommand): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .status(OK)
            .body(loginEmailPasswordUseCase.execute(loginRequestCommand))
    }

    @ExceptionHandler(LoginException::class)
    fun handleLoginException(e: LoginException): ResponseEntity<String> {
        if (e.cause is UserLockedException) {
            return ResponseEntity
                .status(FORBIDDEN)
                .body(e.message)
        }

        return ResponseEntity
            .status(UNAUTHORIZED)
            .body(e.message)
    }
}