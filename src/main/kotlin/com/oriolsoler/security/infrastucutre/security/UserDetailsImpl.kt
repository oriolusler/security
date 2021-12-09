package com.oriolsoler.security.infrastucutre.security

import com.oriolsoler.security.domain.User
import com.oriolsoler.security.domain.user.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

class UserDetailsImpl(val user: User) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.roles.stream()
            .map { role: UserRole ->
                SimpleGrantedAuthority(
                    role.name
                )
            }
            .collect(Collectors.toList())

    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.name
    }

    override fun isAccountNonExpired(): Boolean {
        return false
    }

    override fun isAccountNonLocked(): Boolean {
        return !user.locked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return false
    }

    override fun isEnabled(): Boolean {
        return user.enabled
    }

}