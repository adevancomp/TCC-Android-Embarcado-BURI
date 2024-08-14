package br.edu.uea.buri.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import br.edu.uea.buri.data.database.entity.EquipmentEntity
import br.edu.uea.buri.data.database.entity.UserEntity
import br.edu.uea.buri.data.database.entity.UserWithEquipments
import java.util.UUID

@Dao
interface UserDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: EquipmentEntity)

    @Transaction
    @Insert
    suspend fun insertUserWithoutEquipments(user: UserEntity)

    @Transaction
    @Insert
    suspend fun insertUserWithEquipmentsList(user: UserEntity, equipments: List<EquipmentEntity>){
        insertUserWithoutEquipments(user)
        equipments.forEach {insertEquipment(it)}
    }

    @Query("SELECT * FROM user_app WHERE id=:userId")
    suspend fun getUserWithEquipmentsById(userId: UUID) : UserWithEquipments?

    @Query("DELETE FROM user_app WHERE id = :userId")
    suspend fun deleteUserById(userId: UUID)
}