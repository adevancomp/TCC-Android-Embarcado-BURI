package br.edu.uea.buri.domain.equipment

import java.util.UUID

data class EquipmentRegister(
    val id: String,
    val name: String,
    val ownerId: UUID
)