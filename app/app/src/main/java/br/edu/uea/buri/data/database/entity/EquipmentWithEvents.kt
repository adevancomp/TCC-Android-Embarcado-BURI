package br.edu.uea.buri.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class EquipmentWithEvents(
    @Embedded val equipment: EquipmentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "equipment_id"
    ) val events: List<EventEntity>
)
