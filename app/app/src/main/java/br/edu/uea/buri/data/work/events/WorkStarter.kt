package br.edu.uea.buri.data.work.events

import android.content.Context
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class WorkStarter(context : Context) {
    private val workManager = WorkManager.getInstance(context)

    operator fun invoke() {
        val request = PeriodicWorkRequestBuilder<EventsWorkManager>(20,TimeUnit.MINUTES).build()
        workManager.enqueue(request)
    }
}