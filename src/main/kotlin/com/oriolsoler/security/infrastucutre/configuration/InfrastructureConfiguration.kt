package com.oriolsoler.security.infrastucutre.configuration

import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.application.login.TokenGenerator
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.domain.services.JwtTokenService
import com.oriolsoler.security.infrastucutre.repository.PostgresUserRepository
import com.oriolsoler.security.infrastucutre.security.UserService
import com.oriolsoler.security.infrastucutre.security.filter.AuthTokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class InfrastructureConfiguration {
    @Bean
    fun signUpUserRepository(jdbcTemplate: NamedParameterJdbcTemplate): SignUpUserRepository {
        return PostgresUserRepository(jdbcTemplate)
    }

    @Bean
    fun loginUserRepository(jdbcTemplate: NamedParameterJdbcTemplate): LoginUserRepository {
        return PostgresUserRepository(jdbcTemplate)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun tokenGenerator(@Value("\${jwt.secret}") jwtKey: String): TokenGenerator {
        return JwtTokenService(jwtKey)
    }

    @Bean
    fun authenticationJwtTokenFilter(
        tokenGenerator: TokenGenerator,
        userDetailsService: UserDetailsService
    ): AuthTokenFilter {
        return AuthTokenFilter(tokenGenerator, userDetailsService)
    }

    @Bean
    fun userService(loginUserRepository: LoginUserRepository): UserDetailsService {
        return UserService(loginUserRepository)
    }
}