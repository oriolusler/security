package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.SignUpUserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserRole.ROLE_USER
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals


class SignUpEmailPasswordTestCase {

    @Test
    fun `sign up correctly with email and password`() {
        val email = "user@email.com"
        val password = "password"
        val encryptedPassword = "encrypted_password"
        val name = "Oriol"
        val phone = "+34666225588"
        val roles = listOf(ROLE_USER)

        val user = User(name = name, email = email, phone = phone, password = encryptedPassword, roles = roles)

        val signUpUserRepository = mock<SignUpUserRepository> {
            on { save(email, encryptedPassword, name, phone, roles) } doReturn user
        }

        val passwordEncoder = mock<PasswordEncoder> {
            on { encode(password) } doReturn encryptedPassword
        }

        val signUpEmailPasswordTestCase = SignUpEmailPasswordUseCase(
            signUpUserRepository,
            passwordEncoder
        )

        val signupRequestCommand = SignUpRequestCommand(
            email,
            password,
            name,
            phone,
            roles
        )
        val userCreated = signUpEmailPasswordTestCase.execute(signupRequestCommand)

        assertEquals(user.id, userCreated.id)
        assertEquals(user.password, userCreated.password)
        verify(passwordEncoder, times(1)).encode(password)
        verify(signUpUserRepository, times(1)).save(email, encryptedPassword, name, phone, roles)
    }
}