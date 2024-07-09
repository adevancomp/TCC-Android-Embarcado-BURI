package br.edu.uea.buri.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.io.encoding.Base64

@Component
class CustomAuthFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val encoder: PasswordEncoder,
    private val authManager: AuthenticationManager
) : OncePerRequestFilter() {
    private val START_INDEX_BASIC_AUTH = 6
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val headerAuthorization = request.getHeader("Authorization")
        if(headerAuthorization==null || !headerAuthorization.startsWith("Basic ")){
            filterChain.doFilter(request,response)
            return
        }
        if(request.requestURL.contains("auth")){
            filterChain.doFilter(request,response)
            return
        }
        val basicToken = headerAuthorization.substring(START_INDEX_BASIC_AUTH)
        val basicTokenDecoded = java.util.Base64.getDecoder().decode(basicToken)
        val basicTokenValue = String(basicTokenDecoded)
        val name = basicTokenValue.split(":")[0]
        val password = basicTokenValue.split(":")[1]

        val userDetails = userDetailsService.loadUserByUsername(name)

        if(encoder.matches(password,userDetails.password)){
            val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                name, password
            )
            val authenticationResponse = authManager.authenticate(authenticationRequest)

            SecurityContextHolder.getContext().authentication = authenticationResponse
        }
        filterChain.doFilter(request,response)
    }
}