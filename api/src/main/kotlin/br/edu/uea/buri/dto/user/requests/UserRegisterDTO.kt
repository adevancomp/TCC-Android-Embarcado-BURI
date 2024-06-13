package br.edu.uea.buri.dto.user.requests

import br.edu.uea.buri.domain.UserApp
import br.edu.uea.buri.utils.DomainOperations
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserRegisterDTO(
    @Size(max = 210, message = "Campo name não pode ter mais de 210 caracteres")
    val name: String,
    @Email(message = "Email deve ser válido")
    val email: String,
    @NotBlank
    val password: String,
    @NotBlank
    val role: String
){
    fun toEntity() : UserApp = UserApp(
        name = this.name,
        email = this.email,
        passwordEncrypted = this.password,
        role = DomainOperations.stringToRoleUser(role)
    )
}
