package br.edu.uea.buri.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.edu.uea.buri.data.database.entity.EventEntity

@Dao
interface EventDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Query("SELECT * FROM event WHERE id=:eventId")
    suspend fun getById(eventId: Long) : EventEntity?

    @Query("SELECT * FROM event WHERE equipmentId=:equipmentId ORDER BY date_event DESC")
    suspend fun getAllOrderedByEquipmentId(equipmentId: String) : List<EventEntity>

    @Transaction
    @Query("DELETE FROM event WHERE equipmentId=:equipmentId")
    suspend fun deleteAllByEquipmentId(equipmentId: String)
}