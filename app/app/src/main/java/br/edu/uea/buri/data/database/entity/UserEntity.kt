package br.edu.uea.buri.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user_app")
data class UserEntity(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "email", index = true) val email: String,
)
