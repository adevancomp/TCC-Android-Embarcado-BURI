package br.edu.uea.buri.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.edu.uea.buri.data.database.entity.UserEntity
import br.edu.uea.buri.data.database.entity.UserWithEquipments
import java.util.UUID

@Dao
interface UserDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithoutEquipments(user: UserEntity)

    @Query("SELECT * FROM user_app WHERE id=:userId")
    suspend fun getWithEquipmentsById(userId: UUID) : UserWithEquipments?

    @Query("DELETE FROM user_app WHERE id = :userId")
    suspend fun deleteById(userId: UUID)
}