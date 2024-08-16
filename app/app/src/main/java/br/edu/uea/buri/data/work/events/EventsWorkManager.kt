package br.edu.uea.buri.data.work.events

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import br.edu.uea.buri.R
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.data.database.dao.EquipmentDao
import br.edu.uea.buri.data.database.dao.EventDao
import br.edu.uea.buri.data.database.dao.UserDao
import br.edu.uea.buri.data.database.entity.EventEntity
import br.edu.uea.buri.domain.event.EnviromentEvent
import br.edu.uea.buri.domain.user.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

@HiltWorker
class EventsWorkManager @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val shared: SharedPreferences,
    private val buriApi: BuriApi,
    private val userDao: UserDao,
    private val eventDao: EventDao,
    private val equipmentDao: EquipmentDao
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        success()
    }


}