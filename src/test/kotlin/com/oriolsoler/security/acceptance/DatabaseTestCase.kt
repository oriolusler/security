package com.oriolsoler.security.acceptance

import com.oriolsoler.security.SecurityApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=integration-test"]
)
abstract class DatabaseTestCase {

    @Autowired
    private lateinit var datasource: DataSource

    @Test
    internal fun `should connect to database`() {
        val jdbcTemplate = JdbcTemplate(datasource)

        val actual = jdbcTemplate.queryForObject("SELECT version()", String::class.java)

        assertThat(actual).startsWith("PostgreSQL 12.4")
    }
}