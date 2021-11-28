package com.oriolsoler.security

import com.oriolsoler.security.acceptance.ApplicationTestCase
import com.oriolsoler.security.helper.DockerComposeHelper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested

class SecurityApplicationTests {

    companion object{
        private val dockerCompose = DockerComposeHelper()

        @BeforeAll
        @JvmStatic
        fun dockerComposeUp(){
            dockerCompose.start()
        }

        @AfterAll
        @JvmStatic
        fun dockerComposeDown(){
            dockerCompose.stop()
        }
    }

    @Nested
    inner class Application : ApplicationTestCase()
}
