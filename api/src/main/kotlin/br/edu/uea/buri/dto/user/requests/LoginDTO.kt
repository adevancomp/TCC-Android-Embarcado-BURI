package br.edu.uea.buri.dto.user.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginDTO(
    @NotBlank @Email val email: String,
    @NotBlank val password: String
)