package br.edu.uea.buri.dto.user.requests

import br.edu.uea.buri.domain.UserApp
import br.edu.uea.buri.utils.DomainOperations
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UserRegisterDTO(
    @field:NotEmpty(message = "Campo name não pode ser nulo ou vazio")
    @field:Size(max = 210, message = "Campo name não pode ter mais de 210 caracteres")
    val name: String="",

    @field:NotEmpty(message = "Email não pode ser nulo ou vazio")
    @field:Email(message = "Email deve ser válido")
    val email: String="",

    @field:NotEmpty(message = "Senha não pode ser nula ou vazia")
    val password: String="",
    @field:NotEmpty(message = "Campo role (papel) não pode ser nula ou campo branco")
    val role: String=""
){
    fun toEntity() : UserApp = UserApp(
        name = this.name,
        email = this.email,
        passwordEncrypted = this.password,
        role = DomainOperations.stringToRoleUser(role)
    )
}
