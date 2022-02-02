package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.validateupdatepassword.ValidateUpdatePasswordException
import com.oriolsoler.security.application.validateupdatepassword.ValidateUpdatePasswordUseCase
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.domain.verification.VerificationType.*
import com.oriolsoler.security.infrastucutre.controller.validateupdatepassword.ValidateUpdatedPasswordCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ValidateUpdatePasswordTestCase {

    @Test
    fun `should validate update password verification`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = FORGOT_PASSWORD)
        val validateUpdatedPasswordCommand = ValidateUpdatedPasswordCommand(user.email, verification)
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

        val validateUpdatePasswordUseCase = ValidateUpdatePasswordUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        validateUpdatePasswordUseCase.execute(validateUpdatedPasswordCommand)

        verify(userRepository).getBy(user.email)
        verify(verifyServiceRepository).getBy(user, verification)
        verify(verifyService).checkIfExpired(verificationObject)
        verify(verifyServiceRepository).setToValidated(userVerification)
    }

    @Test
    fun `should return an exception if token is expired`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = FORGOT_PASSWORD)
        val validateUpdatedPasswordCommand = ValidateUpdatedPasswordCommand(user.email, verification)
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

        val validateUpdatePasswordUseCase = ValidateUpdatePasswordUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUpdatePasswordException> {
            validateUpdatePasswordUseCase.execute(validateUpdatedPasswordCommand)
        }
        assertEquals("Validate update password error: Verification expired", exception.message)
    }

    @Test
    fun `should return an exception if token is already validated`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = FORGOT_PASSWORD)
        val validateUpdatedPasswordCommand = ValidateUpdatedPasswordCommand(user.email, verification)
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

        val validateUpdatePasswordUseCase = ValidateUpdatePasswordUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUpdatePasswordException> {
            validateUpdatePasswordUseCase.execute(validateUpdatedPasswordCommand)
        }
        assertEquals("Validate update password error: Verification already used", exception.message)
    }

    @Test
    fun `should return an exception if code does not`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val validateUpdatedPasswordCommand = ValidateUpdatedPasswordCommand(user.email, verification)

        val verifyService = mock<VerifyService> {}
        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        given {
            verifyServiceRepository.getBy(user, verification)
        } willAnswer { throw VerificationNotFoundException() }

        val validateUpdatePasswordUseCase = ValidateUpdatePasswordUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUpdatePasswordException> {
            validateUpdatePasswordUseCase.execute(validateUpdatedPasswordCommand)
        }
        assertEquals("Validate update password error: No verification found", exception.message)
    }

    @Test
    fun `should return an exception if user does not exists`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val validateUpdatedPasswordCommand = ValidateUpdatedPasswordCommand(user.email, verification)

        val verifyService = mock<VerifyService> {}
        val userRepository = mock<UserRepository> {}
        given { userRepository.getBy(user.email) } willAnswer { throw UserNotFoundException() }
        val verifyServiceRepository = mock<VerifyServiceRepository> {}

        val validateUpdatePasswordUseCase = ValidateUpdatePasswordUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUpdatePasswordException> {
            validateUpdatePasswordUseCase.execute(validateUpdatedPasswordCommand)
        }
        assertEquals("Validate update password error: User not found", exception.message)
    }

    @Test
    fun `should return an exception if validation type is not valid`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification(verification = "516797", type = VALIDATE_USER)
        val validateUpdatedPasswordCommand = ValidateUpdatedPasswordCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> { }

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val validateUpdatePasswordUseCase = ValidateUpdatePasswordUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<ValidateUpdatePasswordException> {
            validateUpdatePasswordUseCase.execute(validateUpdatedPasswordCommand)
        }
        assertEquals("Validate update password error: Invalid verification type", exception.message)
    }
}