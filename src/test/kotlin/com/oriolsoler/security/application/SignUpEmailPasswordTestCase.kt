package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oriolsoler.security.application.signup.SignUpEmailPasswordUseCase
import com.oriolsoler.security.application.signup.UserRepository
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.infrastucutre.controller.signup.SignUpRequestCommand
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals


class SignUpEmailPasswordTestCase {

    /*
    private var mockMvc: MockMvc? = null
    private var spyStubUserStore: SpyStubUserStore? = null
    private lateinit var passwordEncoder: PasswordEncoder
    private val mapper: ObjectMapper = ObjectMapper()
    private var user: User? = null
    private var signupRequest: SignupRequestDto? = null

    @BeforeEach
    fun setup() {
        spyStubUserStore = SpyStubUserStore()
        passwordEncoder = mock {}
        mockMvc = MockMvcBuilders
            .standaloneSetup(SignUpController(spyStubUserStore, passwordEncoder))
            .build()
        user = User()
        signupRequest = SignUpRequestCommand("user@email.com", "password")

        spyStubUserStore.setAddUser_return_value(user)
    }
*/
    @Test
    fun `sign up correctly with email and password`() {
        val email = "user@email.com"
        val password = "password"
        val encryptedPassword = "encrypted_password"
        val user = User("name", email, "666225588", encryptedPassword)

        val userRepository = mock<UserRepository> {
            on { save(email, encryptedPassword) } doReturn user
        }

        val passwordEncoder = mock<PasswordEncoder> {
            on { encode(password) } doReturn encryptedPassword
        }

        val signUpEmailPasswordTestCase = SignUpEmailPasswordUseCase(
            userRepository,
            passwordEncoder
        )

        val signupRequestCommand = SignUpRequestCommand(email, password)
        val userCreated = signUpEmailPasswordTestCase.execute(signupRequestCommand)

        assertEquals(user.id, userCreated.id)
        assertEquals(user.password, userCreated.password)
        verify(passwordEncoder, times(1)).encode(password)
        verify(userRepository, times(1)).save(email, encryptedPassword)
    }
}