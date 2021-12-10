package com.oriolsoler.security.infrastucutre.security

import com.oriolsoler.security.application.UserRepository
import com.oriolsoler.security.domain.user.UserId
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService


class UserService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(userId: String): UserDetails {
        val user = userRepository.getBy(UserId(userId))
        return UserDetailsImpl(user)
    }
}
