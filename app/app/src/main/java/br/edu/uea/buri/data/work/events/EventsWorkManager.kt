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
        val userId = shared.getString("id", "")
        if (!userId.isNullOrEmpty()) {
            try {
                val userResponse = buriApi.getUserById(UUID.fromString(userId))
                Log.i("BURI", "User response: $userResponse")

                if (userResponse.isSuccessful) {
                    val user = userResponse.body()
                    if (user != null) {
                        Log.i("BURI", "User email: ${user.email}, Number of equipments: ${user.equipments.size}")

                        user.equipments.forEach { equipment ->
                            Log.i("BURI", "Processing equipment: $equipment")

                            val eventResponse = buriApi.getEvent(equipment.id)
                            Log.i("BURI", "Event response for equipment ${equipment.id}: $eventResponse")

                            if (eventResponse.isSuccessful) {
                                val event = eventResponse.body()
                                if (event != null) {
                                    eventDao.insert(EventEntity(event.id, event.type, event.message, event.date, event.equipmentId!!))
                                } else {
                                    Log.e("BURI", "Event response body is null for equipment ${equipment.id}")
                                }
                            } else {
                                Log.e("BURI", "Failed to fetch event for equipment ${equipment.id}, response code: ${eventResponse.code()}")
                            }

                            val listEvents: List<EventEntity> = eventDao.getAllOrderedByEquipmentId(equipment.id)
                            Log.i("BURI", "Events for equipment ${equipment.id}: $listEvents")
                        }
                    } else {
                        Log.e("BURI", "User response body is null")
                        return@withContext failure()
                    }
                } else {
                    Log.e("BURI", "Failed to fetch user, response code: ${userResponse.code()}")
                    return@withContext failure()
                }

                return@withContext success()
            } catch (e: Exception) {
                Log.e("BURI", "Error during work execution", e)
                return@withContext failure()
            }
        } else {
            Log.e("BURI", "User ID is null or empty")
            return@withContext failure()
        }
    }


}