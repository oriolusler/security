package com.oriolsoler.security.application

import com.nhaarman.mockito_kotlin.*
import com.oriolsoler.security.application.updatepassword.UpdatePasswordException
import com.oriolsoler.security.application.updatepassword.UpdatePasswordUseCase
import com.oriolsoler.security.domain.user.User
import com.oriolsoler.security.domain.verification.*
import com.oriolsoler.security.domain.verification.VerificationType.*
import com.oriolsoler.security.infrastucutre.controller.updatepassword.UpdatePasswordRequestCommand
import com.oriolsoler.security.infrastucutre.repository.user.UserNotFoundException
import com.oriolsoler.security.infrastucutre.repository.verification.VerificationNotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdatePasswordTestCase {
    @Test
    fun `should change password`() {
        val verification = "527832"
        val verificationObject = Verification(verification = verification, type = FORGOT_PASSWORD)
        val mail = "user@email.com"
        val user = User(email = mail)
        val newPassword = "NEW_PASSWORD"
        val encryptedNewPassword = "ENCRYPTED_NEW_PASSWORD"

        val passwordService = mock<PasswordService> {
            on { encode(newPassword) } doReturn encryptedNewPassword
        }

        val userRepository = mock<UserRepository> {
            on { getBy(mail) } doReturn user
        }
        doNothing().`when`(userRepository).updatePassword(user, encryptedNewPassword)

        val userVerification = UserVerification(user, verificationObject)
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }

        val verifyService = mock<VerifyService> {}
        doNothing().`when`(verifyService).checkIfNotValidated(verificationObject)

        val updatePasswordUseCase = UpdatePasswordUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            passwordService
        )

        val updatePasswordCommand = UpdatePasswordRequestCommand(mail, verification, newPassword)
        updatePasswordUseCase.execute(updatePasswordCommand)

        verify(passwordService).encode(newPassword)
        verify(verifyServiceRepository).getBy(user, verification)
        verify(verifyService).checkIfNotValidated(verificationObject)
        verify(verifyService).checkIfUsable(verificationObject)
        verify(verifyServiceRepository).setToUnusable(userVerification)
        verify(userRepository).updatePassword(user, encryptedNewPassword)
        verify(userRepository).getBy(mail)
        assertTrue { true }
    }

    @Test
    fun `should throw exception if user is not found`() {
        val verification = "527832"
        val mail = "user@email.com"
        val newPassword = "NEW_PASSWORD"

        val passwordService = mock<PasswordService> {}
        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        val userRepository = mock<UserRepository> {}
        val verifyService = mock<VerifyService> {}
        given { userRepository.getBy(mail) } willAnswer { throw UserNotFoundException() }

        val updatePasswordUseCase = UpdatePasswordUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            passwordService
        )

        val updatePasswordCommand = UpdatePasswordRequestCommand(mail, verification, newPassword)

        val exception = assertThrows<UpdatePasswordException> {
            updatePasswordUseCase.execute(updatePasswordCommand)
        }
        assertEquals("Update password error: User not found", exception.message)
        verify(userRepository).getBy(mail)
        verify(verifyServiceRepository, times(0)).getBy(any(), any())
        verify(verifyService, times(0)).checkIfNotValidated(any())
        verify(verifyService, times(0)).checkIfUsable(any())
        verify(passwordService, times(0)).encode(any())
        verify(userRepository, times(0)).updatePassword(any(), any())
    }

    @Test
    fun `should throw exception if verification is not found`() {
        val verification = "527832"
        val mail = "user@email.com"
        val user = User(email = mail)
        val newPassword = "NEW_PASSWORD"

        val userRepository = mock<UserRepository> {
            on { getBy(mail) } doReturn user
        }
        val passwordService = mock<PasswordService> {}
        val verifyServiceRepository = mock<VerifyServiceRepository> {}
        val verifyService = mock<VerifyService> {}
        given { verifyServiceRepository.getBy(user, verification) } willAnswer {
            throw VerificationNotFoundException()
        }

        val updatePasswordUseCase = UpdatePasswordUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            passwordService
        )

        val updatePasswordCommand = UpdatePasswordRequestCommand(mail, verification, newPassword)

        val exception = assertThrows<UpdatePasswordException> {
            updatePasswordUseCase.execute(updatePasswordCommand)
        }
        assertEquals("Update password error: No verification found", exception.message)
        verify(userRepository).getBy(mail)
        verify(verifyServiceRepository).getBy(user, verification)
        verify(verifyService, times(0)).checkIfNotValidated(any())
        verify(verifyService, times(0)).checkIfUsable(any())
        verify(passwordService, times(0)).encode(any())
        verify(userRepository, times(0)).updatePassword(any(), any())
    }

    @Test
    fun `should throw exception if verification is not verified`() {
        val verification = "527832"
        val verificationObject = Verification(verification = verification, type = FORGOT_PASSWORD)
        val mail = "user@email.com"
        val user = User(email = mail)
        val newPassword = "NEW_PASSWORD"
        val userVerification = UserVerification(user, verificationObject)

        val userRepository = mock<UserRepository> {
            on { getBy(mail) } doReturn user
        }
        val passwordService = mock<PasswordService> {}
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }
        val verifyService = mock<VerifyService> {}
        given { verifyService.checkIfNotValidated(verificationObject) } willAnswer {
            throw VerificationNotVerifiedException()
        }

        val updatePasswordUseCase = UpdatePasswordUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            passwordService
        )

        val updatePasswordCommand = UpdatePasswordRequestCommand(mail, verification, newPassword)

        val exception = assertThrows<UpdatePasswordException> {
            updatePasswordUseCase.execute(updatePasswordCommand)
        }
        assertEquals("Update password error: Verification has not been verified", exception.message)
        verify(userRepository).getBy(mail)
        verify(verifyServiceRepository).getBy(user, verification)
        verify(verifyService).checkIfNotValidated(verificationObject)
        verify(verifyService, times(0)).checkIfUsable(verificationObject)
        verify(passwordService, times(0)).encode(any())
        verify(userRepository, times(0)).updatePassword(any(), any())
    }

    @Test
    fun `should throw exception if verification is deleted`() {
        val verification = "527832"
        val verificationObject = Verification(verification = verification, type = FORGOT_PASSWORD)
        val mail = "user@email.com"
        val user = User(email = mail)
        val newPassword = "NEW_PASSWORD"
        val userVerification = UserVerification(user, verificationObject)

        val userRepository = mock<UserRepository> {
            on { getBy(mail) } doReturn user
        }
        val passwordService = mock<PasswordService> {}
        val verifyServiceRepository = mock<VerifyServiceRepository> {
            on { getBy(user, verification) } doReturn userVerification
        }
        val verifyService = mock<VerifyService> {}
        doNothing().`when`(verifyService).checkIfNotValidated(verificationObject)
        given { verifyService.checkIfUsable(verificationObject) } willAnswer {
            throw VerificationNotUsableException()
        }

        val updatePasswordUseCase = UpdatePasswordUseCase(
            userRepository,
            verifyService,
            verifyServiceRepository,
            passwordService
        )

        val updatePasswordCommand = UpdatePasswordRequestCommand(mail, verification, newPassword)

        val exception = assertThrows<UpdatePasswordException> {
            updatePasswordUseCase.execute(updatePasswordCommand)
        }
        assertEquals("Update password error: Verification not usable", exception.message)
        verify(userRepository).getBy(mail)
        verify(verifyServiceRepository).getBy(user, verification)
        verify(verifyService).checkIfNotValidated(verificationObject)
        verify(verifyService).checkIfUsable(verificationObject)
        verify(passwordService, times(0)).encode(any())
        verify(userRepository, times(0)).updatePassword(any(), any())
    }
}