package com.oriolsoler.security.infrastucutre.security

import com.oriolsoler.security.application.login.LoginUserRepository
import com.oriolsoler.security.domain.user.UserId
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService


class UserService(private val loginUserRepository: LoginUserRepository) : UserDetailsService {
    override fun loadUserByUsername(userId: String): UserDetails {
        val user = loginUserRepository.getBy(UserId(userId))
        return UserDetailsImpl(user)
    }
}
