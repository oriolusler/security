package com.oriolsoler.security

import com.oriolsoler.security.acceptance.ApplicationTestCase
import com.oriolsoler.security.acceptance.DatabaseTestCase
import com.oriolsoler.security.acceptance.LoginWithEmailPasswordTestCase
import com.oriolsoler.security.acceptance.SignUpWithEmailPasswordTestCase
import com.oriolsoler.security.helper.DockerComposeHelper
import com.oriolsoler.security.infrastucutre.repository.UserRepositoryTestCase
import com.oriolsoler.security.infrastucutre.repository.VerificationRepositoryTestCase
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested

class SecurityApplicationTests {

    companion object {
        private val dockerCompose = DockerComposeHelper()

        @BeforeAll
        @JvmStatic
        fun dockerComposeUp() {
            dockerCompose.start()
        }

        @AfterAll
        @JvmStatic
        fun dockerComposeDown() {
            dockerCompose.stop()
        }
    }

    @Nested
    inner class Application : ApplicationTestCase()

    @Nested
    inner class Database : DatabaseTestCase()

    @Nested
    inner class SignUpWithEmailPassword : SignUpWithEmailPasswordTestCase()

    @Nested
    inner class SignUpUserRepository : UserRepositoryTestCase()

    @Nested
    inner class LoginWithEmailPassword : LoginWithEmailPasswordTestCase()

    @Nested
    inner class VerificationRepository : VerificationRepositoryTestCase()
}
