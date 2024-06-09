package br.edu.uea.buri.controller

import br.edu.uea.buri.dto.user.requests.UserRegisterDTO
import br.edu.uea.buri.service.IUserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserResource(
    private val userService: IUserService
) {
    @PostMapping
    fun save(@RequestBody @Valid dto: UserRegisterDTO) : ResponseEntity<>
}