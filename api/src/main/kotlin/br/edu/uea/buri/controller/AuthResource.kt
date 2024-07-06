package br.edu.uea.buri.controller

import br.edu.uea.buri.dto.user.requests.LoginDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthResource (
    private val authManager: AuthenticationManager
){
    @PostMapping
    fun login(@RequestBody @Valid dto: LoginDTO) : ResponseEntity<Authentication>{
        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                dto.email, dto.password
        )
        val authenticationResponse = authManager.authenticate(authenticationRequest)
        SecurityContextHolder.getContext().authentication = authenticationResponse
        return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse)
    }
}