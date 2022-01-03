package com.oriolsoler.security.infrastucutre.controller.validaterefreshtoken

import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenException
import com.oriolsoler.security.application.validaterefreshtoken.ValidateRefreshTokenUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ValidateRefreshTokenController(
    private val validateRefreshTokenUseCase: ValidateRefreshTokenUseCase
) {

    @GetMapping("/api/auth/refresh")
    fun validate(validateRefreshTokenCommand: ValidateRefreshTokenCommand) = ResponseEntity
        .status(OK)
        .body(validateRefreshTokenUseCase.execute(validateRefreshTokenCommand))

    @ExceptionHandler(ValidateRefreshTokenException::class)
    fun handleValidateRefreshTokenException(e: ValidateRefreshTokenException) = ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(e.message)
}