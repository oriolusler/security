package com.oriolsoler.security

import com.oriolsoler.security.acceptance.ApplicationTestCase
import com.oriolsoler.security.acceptance.DatabaseTestCase
import com.oriolsoler.security.acceptance.SignUpTestCase
import com.oriolsoler.security.helper.DockerComposeHelper
import com.oriolsoler.security.infrastucutre.repository.UserRepositoryTestCase
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
    inner class SignUp : SignUpTestCase()

    @Nested
    inner class UserRepository : UserRepositoryTestCase()
}
