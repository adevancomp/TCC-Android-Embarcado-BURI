package br.edu.uea.buri.domain.user

data class UserRegister(
    var name: String,
    var email: String,
    var password: String,
    var role: String
)