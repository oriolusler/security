package com.oriolsoler.security.acceptance

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.GoogleCredentials.fromStream
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord
import com.google.firebase.internal.FirebaseProcessEnvironment.setenv
import com.oriolsoler.security.SecurityApplication
import com.oriolsoler.security.infrastucutre.repository.test.UserRepositoryForTest
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus.*
import org.springframework.test.web.servlet.MockMvc
import java.io.FileInputStream
import kotlin.test.assertNotNull


private const val TEST_PROJECT_ID = "signin-a16b6"
private const val AUTH_EMULATOR = "localhost:9099"
private const val FIREBASE_USER_ID = "7l2EVYQcmCWDELEm78E3LtnUjYFj"
private const val PASSWORD = "password"

@SpringBootTest(
    classes = [SecurityApplication::class],
    properties = ["spring.profiles.active=test"]
)
@AutoConfigureMockMvc
abstract class LoginWithGoogleTestCase {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var userRepositoryFotTest: UserRepositoryForTest

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUserRequest: UserRecord.CreateRequest

    init {
        setenv("FIREBASE_AUTH_EMULATOR_HOST", AUTH_EMULATOR)
        setenv("GCLOUD_PROJECT", TEST_PROJECT_ID)

        val options = FirebaseOptions.builder()
            .setCredentials(fromStream(FileInputStream("src/test/resources/firebase-project.json")))
            .build()

        FirebaseApp.initializeApp(options)

        auth = FirebaseAuth.getInstance()

        firebaseUserRequest = UserRecord.CreateRequest()
            .setUid(FIREBASE_USER_ID)
            .setDisplayName("Alfred")
            .setEmail("email@online.com")
            .setPassword(PASSWORD)
            .setPhoneNumber("+34666118833")
    }

    @BeforeEach
    fun setUp() {
        RestAssuredMockMvc.mockMvc(mvc)
        userRepositoryFotTest.clean()
        try {
            auth.deleteUser(FIREBASE_USER_ID)
        } catch (_: FirebaseAuthException) {
        }
    }

    @Test
    internal fun `should create user into firebase emulator`() {
        val user = auth.createUser(firebaseUserRequest)
        assertNotNull(user)
    }
/*
    @Test
    internal fun `should login successfully`() {
        val firebaseUser = auth.createUser(firebaseUserRequest)
        val firebaseToken = FirebaseAuth.getInstance().createCustomToken(firebaseUser.uid)
        val loginWithGoogleRequestCommand = LoginWithGoogleRequestCommand(firebaseToken)
        given()
            .contentType("application/json")
            .body(loginWithGoogleRequestCommand)
            .post("/api/auth/login/google")
            .then()
            .status(OK)
            .body("user.id", equalTo(user.id.value.toString()))
            .body("user.email", equalTo("cabot1997@gmail.com"))
            .body("token.accessToken", notNullValue())
            .body("token.refreshToken", notNullValue())
    }*/
}