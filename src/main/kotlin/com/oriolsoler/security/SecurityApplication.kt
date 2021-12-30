package com.oriolsoler.security

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.oriolsoler.security.infrastucutre.configuration",
        "com.oriolsoler.security.infrastucutre.controller"
    ]
)
@EnableEncryptableProperties
class SecurityApplication

fun main(args: Array<String>) {
    runApplication<SecurityApplication>(*args)
}
