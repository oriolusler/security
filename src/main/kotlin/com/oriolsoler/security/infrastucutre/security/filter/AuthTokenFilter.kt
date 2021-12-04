package com.oriolsoler.security.infrastucutre.security.filter

import com.oriolsoler.security.application.login.TokenGenerator
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class AuthTokenFilter(
    private val tokenService: TokenGenerator,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        // Get authorization header and validate
        val header = request.getHeader(AUTHORIZATION);
        if (header.isNullOrEmpty() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // Get jwt token and validate
        val token = header.split(" ")[1].trim();
        if (!tokenService.isValid(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val userId = tokenService.getUserIdFromToken(token)
        val userDetails: UserDetails = userDetailsService.loadUserByUsername(userId)

        val authentication = UsernamePasswordAuthenticationToken(
            userDetails, null,
            userDetails.authorities
        )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)

    }
}