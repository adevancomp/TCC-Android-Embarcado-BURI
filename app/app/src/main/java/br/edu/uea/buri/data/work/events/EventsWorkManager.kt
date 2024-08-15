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
        Log.d("BURI","executou o work")
        try {
            if (!shared.getString("id", "").isNullOrEmpty()) {
                val response: Response<User> = buriApi.getUserById(UUID.fromString(shared.getString("id", "")))
                if(response.isSuccessful){
                    response.body()?.let {
                        user ->
                            user.equipments.forEach {
                                equipment ->
                                    //Para cada equipment, faça o seguinte :
                                    //      - Dê um GET no /event
                                    //      - Verifique se o ultimo evento salvo no banco de dados tem a mesma mensagem
                                    //      - Se tiver, então notifica, se não então cria uma notificação
                                val responseEvent: Response<EnviromentEvent?> = buriApi.getEvent(equipment.id)
                                if(responseEvent.isSuccessful){
                                    if(responseEvent.body()!=null){
                                        val listEvents =
                                            userDao.getAllEventsByEquipmentId(equipment.id)
                                        if(listEvents.isEmpty()){
                                            responseEvent.body()?.let {
                                                userDao.insertEvent(
                                                    EventEntity(
                                                        id= it.id,
                                                        type = it.type,
                                                        message = it.message,
                                                        equipmentId = it.equipmentId!!
                                                    )
                                                )
                                            }
                                        } else {
                                            //Lista não vazia, já tinha um evento lá
                                            val lastEvent : EventEntity = listEvents.first()
                                            responseEvent.body()?.let {
                                                newEventResponse ->

                                                if(lastEvent.message!=newEventResponse.message){
                                                    //Grava e notifica
                                                    userDao.insertEvent(
                                                        EventEntity(
                                                            id= newEventResponse.id,
                                                            type = newEventResponse.type,
                                                            message = newEventResponse.message,
                                                            equipmentId = newEventResponse.equipmentId!!
                                                        )
                                                    )
                                                    //Parte de notificar o usuário
                                                    sendNotification(newEventResponse.type, message = newEventResponse.message)
                                                    //Fim da parte de notificar o usuário
                                                } else {
                                                    //Só grava
                                                    userDao.insertEvent(
                                                        EventEntity(
                                                            id= newEventResponse.id,
                                                            type = newEventResponse.type,
                                                            message = newEventResponse.message,
                                                            equipmentId = newEventResponse.equipmentId!!
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
            }
            success()
        } catch (ex: Exception){
            failure()
        }
    }
    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "RISK_EVENT_CHANNEL"

        val channel = NotificationChannel(
            channelId,
            "Alertas de Risco",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.all_inclusive)
            .build()

        notificationManager.notify(1, notification)
    }
}