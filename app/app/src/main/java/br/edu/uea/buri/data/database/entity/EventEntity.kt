package br.edu.uea.buri.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(
    tableName = "event",
    foreignKeys = [ForeignKey(
        entity = EquipmentEntity::class,
        parentColumns = ["id"],
        childColumns = ["equipment_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class EventEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo("type") val type: String,
    @ColumnInfo("message") val message: String,
    @ColumnInfo("date_event") val dateEvent: ZonedDateTime,
    @ColumnInfo("equipment_id") val equipmentId: String
)
