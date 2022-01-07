package com.oriolsoler.security.infrastucutre.controller.validaterefreshtoken

import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenException
import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenUseCase
import com.oriolsoler.security.domain.Token
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ValidateRefreshTokenController(
    private val validateRefreshTokenUseCase: ValidateRefreshTokenUseCase
) {
    @PostMapping("/api/auth/refresh")
    fun validate(@RequestBody validateRefreshTokenCommand: ValidateRefreshTokenCommand): ResponseEntity<Token> {
        return ResponseEntity
            .status(OK)
            .body(validateRefreshTokenUseCase.execute(validateRefreshTokenCommand))
    }

    @ExceptionHandler(ValidateRefreshTokenException::class)
    fun handleValidateRefreshTokenException(e: ValidateRefreshTokenException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(e.message)
    }
}