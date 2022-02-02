package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.validateuser.ValidateUserException
import com.oriolsoler.security.application.validateuser.ValidateUserUseCase
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.domain.verification.VerificationType.*
import com.oriolsoler.security.infrastucutre.controller.validateuser.ValidateUserCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ValidateUserTestCase {

    @Test
    fun `should validate user signup verification`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = VALIDATE_USER)
        val validateUserCommand = ValidateUserCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> {}
        doNothing().`when`(verifyService).checkIfExpired(verificationObject)
        doNothing().`when`(verifyService).checkIfUsable(verificationObject)

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val validateUserUseCase = ValidateUserUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        validateUserUseCase.execute(validateUserCommand)

        verify(userRepository).getBy(user.email)
        verify(verifyServiceRepository).getBy(user, verification)
        verify(verifyService).checkIfExpired(verificationObject)
        verify(verifyServiceRepository).setToValidated(userVerification)
        verify(userRepository).setUnlocked(user)
    }

    @Test
    fun `should return an exception if validation is expired`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = VALIDATE_USER)
        val validateUserCommand = ValidateUserCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> { }
        given {
            verifyService.checkIfExpired(verificationObject)
        } willAnswer { throw VerificationExpiredException() }

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val validateUserUseCase = ValidateUserUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUserException> {
            validateUserUseCase.execute(validateUserCommand)
        }
        assertEquals("Validate user error: Verification expired", exception.message)
    }

    @Test
    fun `should return an exception if validation is already validated`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = VALIDATE_USER)
        val validateUserCommand = ValidateUserCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> { }
        given {
            verifyService.checkIfAlreadyValidated(verificationObject)
        } willAnswer { throw VerificationAlreadyVerifiedException() }

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val validateUserUseCase = ValidateUserUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUserException> {
            validateUserUseCase.execute(validateUserCommand)
        }
        assertEquals("Validate user error: Verification already used", exception.message)
    }

    @Test
    fun `should return an exception if validation does not exists`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val validateUserCommand = ValidateUserCommand(user.email, verification)

        val verifyService = mock<VerifyService> {}
        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        given {
            verifyServiceRepository.getBy(user, verification)
        } willAnswer { throw VerificationNotFoundException() }

        val validateUserUseCase = ValidateUserUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUserException> {
            validateUserUseCase.execute(validateUserCommand)
        }
        assertEquals("Validate user error: No verification found", exception.message)
    }

    @Test
    fun `should return an exception if user does not exists`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val validateUserCommand = ValidateUserCommand(user.email, verification)

        val verifyService = mock<VerifyService> {}
        val userRepository = mock<UserRepository> {}
        given { userRepository.getBy(user.email) } willAnswer { throw UserNotFoundException() }
        val verifyServiceRepository = mock<VerifyServiceRepository> {}

        val validateUserUseCase = ValidateUserUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUserException> {
            validateUserUseCase.execute(validateUserCommand)
        }
        assertEquals("Validate user error: User not found", exception.message)
    }

    @Test
    fun `should return an exception if validation is not usable`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = VALIDATE_USER)
        val validateUserCommand = ValidateUserCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> { }
        given {
            verifyService.checkIfUsable(verificationObject)
        } willAnswer { throw VerificationNotUsableException() }

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val validateUserUseCase = ValidateUserUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUserException> {
            validateUserUseCase.execute(validateUserCommand)
        }
        assertEquals("Validate user error: Verification not usable", exception.message)
    }

    @Test
    fun `should return an exception if validation type is not valid`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = FORGOT_PASSWORD)
        val validateUserCommand = ValidateUserCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> { }

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val validateUserUseCase = ValidateUserUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUserException> {
            validateUserUseCase.execute(validateUserCommand)
        }
        assertEquals("Validate user error: Invalid verification type", exception.message)
    }
}