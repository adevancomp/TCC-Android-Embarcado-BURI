package br.edu.uea.buri.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "equipment",
    foreignKeys=[ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class EquipmentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "user_id") val userId: UUID
)