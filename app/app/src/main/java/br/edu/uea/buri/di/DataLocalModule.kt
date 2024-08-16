package br.edu.uea.buri.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import br.edu.uea.buri.data.database.AppDatabase
import br.edu.uea.buri.data.database.dao.EquipmentDao
import br.edu.uea.buri.data.database.dao.EventDao
import br.edu.uea.buri.data.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataLocalModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("buri_shared", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
             "buri_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase) : UserDao{
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideEventDao(appDatabase: AppDatabase) : EventDao{
        return appDatabase.eventDao()
    }

    @Provides
    @Singleton
    fun provideEquipmentDao(appDatabase: AppDatabase) : EquipmentDao{
        return appDatabase.equipmentDao()
    }
}