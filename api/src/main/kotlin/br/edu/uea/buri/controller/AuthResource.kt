package br.edu.uea.buri.controller

import br.edu.uea.buri.dto.user.requests.LoginDTO
import br.edu.uea.buri.dto.user.requests.UserRegisterDTO
import br.edu.uea.buri.dto.user.views.UserViewDTO
import br.edu.uea.buri.service.IUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthResource (
    private val authManager: AuthenticationManager,
    private val userService: IUserService,
    private val encoder: PasswordEncoder
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
    @PostMapping("/register")
    fun save(@RequestBody @Valid dto: UserRegisterDTO) : ResponseEntity<UserViewDTO>{
        val user = dto.toEntity()
        user.passwordEncrypted = encoder.encode(user.passwordEncrypted)
        val userSaved = userService.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved.toUserViewDTO())
    }
}