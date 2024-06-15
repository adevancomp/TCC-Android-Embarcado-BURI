package br.edu.uea.buri.dto.equipment.requests

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.utils.DomainOperations
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

data class EquipmentRegisterDTO(
    @Size(max = 6, min = 6, message = "Um ID tem de ter exatamente 6 caracteres de string")
    val id: String? = null,
    @NotEmpty(message = "Nome pode ser vazio ou branco") @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    val name: String = "",
    @NotNull(message = "O primeiro cadastro exige um ID de usuário")
    val ownerId: UUID
){
    fun toEntity() = Equipment(
        id = this.id ?: DomainOperations.generateEquipmentId(),
        name = this.name
    )
}