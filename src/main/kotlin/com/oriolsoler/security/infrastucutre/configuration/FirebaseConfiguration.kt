package com.oriolsoler.security.infrastucutre.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.core.io.Resource
import java.io.InputStream


@Configuration
class FirebaseConfiguration {

    @Value(value = "classpath:firebase-project.json")
    private lateinit var serviceAccountResource: Resource

    @Bean
    fun createFirebaseApp(): FirebaseApp {
        val serviceAccount: InputStream = serviceAccountResource.inputStream
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        return if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        } else {
            FirebaseApp.getInstance()
        }
    }

    @Bean
    @DependsOn(value = ["createFirebaseApp"])
    fun createFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
