package br.edu.uea.buri.controller

import br.edu.uea.buri.dto.user.requests.UserRegisterDTO
import br.edu.uea.buri.dto.user.views.UserViewDTO
import br.edu.uea.buri.service.IUserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/user")
class UserResource(
    private val userService: IUserService,
    private val encoder: PasswordEncoder
) {
    @PostMapping
    fun save(@RequestBody @Valid dto: UserRegisterDTO) : ResponseEntity<UserViewDTO>{
        val user = dto.toEntity()
        user.passwordEncrypted = encoder.encode(user.passwordEncrypted)
        val userSaved = userService.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved.toUserViewDTO())
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID) : ResponseEntity<UserViewDTO>{
        val userView = userService.findById(id).toUserViewDTO()
        return ResponseEntity.status(HttpStatus.OK).body(userView)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: UUID){
        userService.deleteById(id)
    }
}