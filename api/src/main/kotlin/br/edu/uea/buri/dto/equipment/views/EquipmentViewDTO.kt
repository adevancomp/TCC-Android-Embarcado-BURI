package br.edu.uea.buri.dto.equipment.views

import java.util.*

data class EquipmentViewDTO(
    val id: String,
    val name: String,
    val ownerId: UUID?
)
