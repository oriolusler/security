package com.oriolsoler.security.application.login

interface TokenGenerator {
    fun generate(any: Any): Token
}