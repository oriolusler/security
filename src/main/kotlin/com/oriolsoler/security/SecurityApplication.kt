package com.oriolsoler.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.oriolsoler.security.infrastucutre.configuration",
        "com.oriolsoler.security.infrastucutre.controller"
    ]
)
class SecurityApplication

fun main(args: Array<String>) {
    runApplication<SecurityApplication>(*args)
}
