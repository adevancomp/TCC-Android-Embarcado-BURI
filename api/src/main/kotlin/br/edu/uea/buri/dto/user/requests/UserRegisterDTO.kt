package br.edu.uea.buri.dto.user.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserRegisterDTO(
    @Size(max = 210, message = "Campo name não pode ter mais de 210 caracteres")
    val name: String,
    @Email(message = "Email deve ser válido")
    val email: String,
    @NotBlank
    val password: String
)
