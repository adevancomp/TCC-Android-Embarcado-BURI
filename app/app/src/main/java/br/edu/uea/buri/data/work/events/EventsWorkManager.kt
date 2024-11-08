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
        val userId = shared.getString("id", "").orEmpty()

        if (userId.isEmpty()) return@withContext failure()

        val responseUser = buriApi.getUserById(UUID.fromString(userId))
        if (!responseUser.isSuccessful) return@withContext failure()

        responseUser.body()?.equipments?.forEach { equipment ->
            val eventsList = eventDao.getAllOrderedByEquipmentId(equipment.id)
            val latestEvent = eventsList.firstOrNull()
            val responseEvent = buriApi.getEvent(equipment.id)

            if (responseEvent.isSuccessful) {
                responseEvent.body()?.let { event ->
                    val shouldInsert = latestEvent == null ||
                            latestEvent.type != event.type ||
                            latestEvent.message != event.message ||
                            event.date.isAfter(latestEvent.dateEvent)

                    if (shouldInsert) {
                        Log.i("BURI","Colocou event no ROOM $event")
                        eventDao.insert(EventEntity(event.id, event.type, event.message, event.date, event.equipmentId!!))
                        sendNotification(event)
                    }
                }
            } else {
                Log.i("BURI","Erro ao chamar event de ${equipment.id} code ${responseEvent.code()}")
            }
        }
        success()
    }

    private fun sendNotification(event: EnviromentEvent) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            "event_id",
            "Event Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(applicationContext, "event_id")
            .setSmallIcon(R.drawable.tasks)
            .setContentTitle("Novo Evento: ${event.type}")
            .setContentText(event.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("Mensagem: ${event.message}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(event.id.toInt(), notification)
    }

}