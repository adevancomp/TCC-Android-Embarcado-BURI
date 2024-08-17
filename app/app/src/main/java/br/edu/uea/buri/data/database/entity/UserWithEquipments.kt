package br.edu.uea.buri.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithEquipments(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    ) val equipments: List<EquipmentEntity>
)
