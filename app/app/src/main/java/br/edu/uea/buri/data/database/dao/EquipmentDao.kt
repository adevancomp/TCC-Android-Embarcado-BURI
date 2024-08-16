package br.edu.uea.buri.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.edu.uea.buri.data.database.entity.EquipmentEntity

@Dao
interface EquipmentDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipment: EquipmentEntity)

    @Query("SELECT * FROM equipment WHERE id=:equipmentId")
    suspend fun getById(equipmentId: String) : EquipmentEntity?

    @Transaction
    @Query("DELETE FROM equipment WHERE id=:equipmentId")
    suspend fun deleteById(equipmentId: String)
}