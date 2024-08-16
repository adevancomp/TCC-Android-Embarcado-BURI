package br.edu.uea.buri.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.edu.uea.buri.data.database.converter.ZonedDateTimeConverter
import br.edu.uea.buri.data.database.dao.EquipmentDao
import br.edu.uea.buri.data.database.dao.EventDao
import br.edu.uea.buri.data.database.dao.UserDao
import br.edu.uea.buri.data.database.entity.EquipmentEntity
import br.edu.uea.buri.data.database.entity.EventEntity
import br.edu.uea.buri.data.database.entity.UserEntity

@Database(entities = [UserEntity::class, EquipmentEntity::class, EventEntity::class], version = 1)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun eventDao() : EventDao
    abstract fun equipmentDao() : EquipmentDao
}