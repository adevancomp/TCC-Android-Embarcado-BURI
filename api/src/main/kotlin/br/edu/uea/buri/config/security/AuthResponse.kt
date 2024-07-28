package br.edu.uea.buri.config.security

import java.util.UUID

data class AuthResponse(
    val authenticated: Boolean,
    val name: String,
    val id: UUID
)
