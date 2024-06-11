package br.edu.uea.buri.dto.equipment.requests

import br.edu.uea.buri.domain.Equipment
import br.edu.uea.buri.utils.DomainOperations
import java.util.UUID

data class EquipmentRegisterDTO(
    val id: String?,
    val name: String,
    val ownerId: UUID
){
    fun toEntity() = Equipment(
        id = this.id ?: DomainOperations.generateEquipmentId(),
        name = this.name
    )
}