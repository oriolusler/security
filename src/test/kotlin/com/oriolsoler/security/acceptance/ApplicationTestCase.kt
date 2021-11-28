package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=integration-test"]
)
@AutoConfigureMockMvc
abstract class ApplicationTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc(mvc)
    }

    @Test
    fun should_be_healthy() {
        given()
            .`when`()
            .get("/actuator/health")
            .then()
            .assertThat(status().isOk)
            .body("status", equalTo("UP"))
    }
}