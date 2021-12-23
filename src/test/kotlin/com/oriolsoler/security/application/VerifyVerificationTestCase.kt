package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.validateverification.VerifyService
import com.oriolsoler.security.application.validateverification.VerifyServiceRepository
import com.oriolsoler.security.application.validateverification.VerifyVerificationUseCase
import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.UserVerification
import com.oriolsoler.security.domain.Verification
import com.oriolsoler.security.infrastucutre.controller.verifyVerification.VerifyVerificationCommand
import org.junit.jupiter.api.Test

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
}