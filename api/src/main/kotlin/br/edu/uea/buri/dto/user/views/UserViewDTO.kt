package br.edu.uea.buri.dto.user.views

import br.edu.uea.buri.dto.equipment.views.EquipmentViewDTO
import java.util.UUID

data class UserViewDTO(
    val id: UUID,
    val name: String,
    val email: String,
    val role: String,
    val equipments: List<EquipmentViewDTO>
)