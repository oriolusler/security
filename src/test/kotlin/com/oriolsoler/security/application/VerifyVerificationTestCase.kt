package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.validateverification.VerifyException
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class VerifyVerificationTestCase {

    @Test
    fun `should verify verification`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification("516797")
        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> {}
        doNothing().`when`(verifyService).validateIfExpired(verificationObject)
        doNothing().`when`(verifyService).validateIfNotDeleted(verificationObject)

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val verifyVerificationUseCase = VerifyVerificationUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        verifyVerificationUseCase.execute(verifyVerificationCommand)

        verify(userRepository, times(1)).getBy(user.email)
        verify(verifyServiceRepository, times(1)).getBy(user, verification)
        verify(verifyService, times(1)).validateIfExpired(verificationObject)
        verify(verifyServiceRepository, times(1)).setToUsed(userVerification)
        verify(userRepository, times(1)).setUnlocked(user)
    }

    @Test
    fun `should return an exception is token is not valid`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verificationObject = Verification("516797")
        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification)
        val userVerification = UserVerification(user, verificationObject)

        val verifyService = mock<VerifyService> { }
        given {
            verifyService.validateIfExpired(verificationObject)
        } willAnswer { throw VerificationExpiredException() }

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val verifyVerificationUseCase = VerifyVerificationUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<VerifyException> { verifyVerificationUseCase.execute(verifyVerificationCommand) }
        assertEquals("Verify error: Verification expired", exception.message)
    }

    @Test
    fun `should return an exception if code does not`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification)

        val verifyService = mock<VerifyService> {}
        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        given {
            verifyServiceRepository.getBy(user, verification)
        } willAnswer { throw VerificationNotFoundException() }

        val verifyVerificationUseCase = VerifyVerificationUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<VerifyException> { verifyVerificationUseCase.execute(verifyVerificationCommand) }
        assertEquals("Verify error: No verification found", exception.message)
    }

    @Test
    fun `should return an exception if user does not exists`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification)

        val verifyService = mock<VerifyService> {}
        val userRepository = mock<UserRepository> {}
        given { userRepository.getBy(user.email) } willAnswer { throw UserNotFoundException() }
        val verifyServiceRepository = mock<VerifyServiceRepository> {}

        val verifyVerificationUseCase = VerifyVerificationUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<VerifyException> { verifyVerificationUseCase.execute(verifyVerificationCommand) }
        assertEquals("Verify error: User not found", exception.message)
    }
}