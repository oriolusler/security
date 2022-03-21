package com.oriolsoler.security.infrastucutre.controller.login.emailpassword

import com.oriolsoler.security.application.login.userpassword.LoginEmailPasswordUseCase
import com.oriolsoler.security.application.login.LoginException
import com.oriolsoler.security.domain.user.UserLockedException
import com.oriolsoler.security.infrastucutre.controller.login.LoginResponse
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
    fun login(@RequestBody loginEmailPasswordRequestCommand: LoginEmailPasswordRequestCommand): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .status(OK)
            .body(loginEmailPasswordUseCase.execute(loginEmailPasswordRequestCommand))
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