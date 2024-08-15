package br.edu.uea.buri.di

import android.content.Context
import br.edu.uea.buri.data.work.events.WorkStarter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    @Singleton
    @Provides
    fun provideWorkerStarter(@ApplicationContext context: Context) = WorkStarter(context)
}