package com.oriolsoler.security.infrastucutre.configuration

import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMConfig
import com.ulisesbocchio.jasyptspringboot.encryptor.SimpleGCMStringEncryptor
import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class JaspyConfiguration {
    @Bean("encryptorBean")
    fun stringEncryptor(
        @Value("\${jasypt.encryptor.gcm-secret-key-password}") jaspytPassword: String,
        @Value("\${jasypt.encryptor.gcm-secret-key-salt}") jaspytSalt: String,
        @Value("\${jasypt.encryptor.gcm-secret-key-algorithm}") jaspytAlgorithm: String
    ): StringEncryptor {
        val config = SimpleGCMConfig()
        config.secretKeyPassword = jaspytPassword
        config.secretKeyIterations = 1000
        config.secretKeySalt = jaspytSalt
        config.secretKeyAlgorithm = jaspytAlgorithm
        return SimpleGCMStringEncryptor(config)
    }
}