package br.edu.uea.buri.config.security

data class AuthResponse(
    val authenticated: Boolean,
    val name: String
)
