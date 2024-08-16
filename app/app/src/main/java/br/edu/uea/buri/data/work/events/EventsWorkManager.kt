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
import br.edu.uea.buri.data.database.dao.UserDao
import br.edu.uea.buri.data.database.entity.EventEntity
import br.edu.uea.buri.domain.event.EnviromentEvent
import br.edu.uea.buri.domain.user.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.UUID

@HiltWorker
class EventsWorkManager @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val shared: SharedPreferences,
    private val buriApi: BuriApi,
    private val userDao: UserDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result  = withContext(Dispatchers.IO){
        if(!shared.getString("id", "").isNullOrEmpty()){
            Log.i("BURI","ID do shared: ${shared.getString("id", "")}")

            val responseUser = buriApi.getUserById(UUID.fromString(shared.getString("id", "")))
            if(responseUser.isSuccessful){
                Log.i("BURI","Retrofit: ${responseUser.body()}")
                responseUser.body()?.let {
                    user ->
                        user.equipments.forEach { equipment ->
                            Log.i("BURI",equipment.toString())
                            val eventsList = userDao.getAllEventsByEquipmentId(equipment.id)
                            Log.i("BURI","Room events list (size=${eventsList.size}): $eventsList")
                            val lastEvent = eventsList.first()
                            val newEventResponse = buriApi.getEvent(equipment.id)
                            if(newEventResponse.isSuccessful){
                                newEventResponse.body()?.let {
                                    newEvent ->
                                        Log.i("BURI","equipmentId:${newEvent.equipmentId} last event: $lastEvent")
                                        Log.i("BURI","equipmentId:${newEvent.equipmentId} new event: $newEvent")
                                }
                            } else {
                                Log.i("BURI","Response de equipment event deu erro ${newEventResponse.code()}")
                            }
                        }
                }
            } else{
                Log.i("BURI","Response de user sem sucesso ${responseUser.code()}")
            }

            success()
        } else {
            failure()
        }
    }

}