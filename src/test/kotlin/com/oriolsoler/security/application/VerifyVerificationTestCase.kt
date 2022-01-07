package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.validateverification.VerifyException
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.domain.verification.VerificationException
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import com.oriolsoler.security.infrastucutre.repository.verification.VerifyRepositoryError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class VerifyVerificationTestCase {

    @Test
    fun `should verify verification`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification)
        val userVerification = UserVerification(user, Verification(verification = verification))

        val verifyService = mock<VerifyService> {
            on { isValid(any()) } doReturn true
        }
        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getUnusedBy(user, verification) } doReturn userVerification
        }

        val verifyVerificationUseCase = VerifyVerificationUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        verifyVerificationUseCase.execute(verifyVerificationCommand)

        verify(userRepository, times(1)).getBy(user.email)
        verify(verifyServiceRepository, times(1)).getUnusedBy(user, verification)
        verify(verifyService, times(1)).isValid(any())
        verify(verifyServiceRepository, times(1)).setToUsed(userVerification)
        verify(userRepository, times(1)).setUnlocked(user)
    }

    @Test
    fun `should return an exception is token is not valid`() {
        val user = User(email = "email@online.com")
        val verification = "516797"
        val verifyVerificationCommand = VerifyVerificationCommand(user.email, verification)
        val userVerification = UserVerification(user, Verification(verification = verification))

        val verifyService = mock<VerifyService> { }
        given {
            verifyService.isValid(any())
        } willAnswer { throw VerificationException("Invalid verification") }

        val userRepository = mock<UserRepository> {
            on { getBy(user.email) } doReturn user
        }
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getUnusedBy(user, verification) } doReturn userVerification
        }

        val verifyVerificationUseCase = VerifyVerificationUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<VerifyException> { verifyVerificationUseCase.execute(verifyVerificationCommand) }
        assertEquals("Verification error: Invalid verification", exception.message)
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
            verifyServiceRepository.getUnusedBy(user, verification)
        } willAnswer { throw VerifyRepositoryError("Token not found") }

        val verifyVerificationUseCase = VerifyVerificationUseCase(
            verifyService,
            verifyServiceRepository,
            userRepository
        )

        val exception = assertThrows<VerifyException> { verifyVerificationUseCase.execute(verifyVerificationCommand) }
        assertEquals("Verification error: Verify repository error: Token not found", exception.message)

    }
}