package br.edu.uea.buri.data

import br.edu.uea.buri.domain.equipment.Equipment
import br.edu.uea.buri.domain.equipment.EquipmentNewId
import br.edu.uea.buri.domain.equipment.EquipmentRegister
import br.edu.uea.buri.domain.measurement.Measurement
import br.edu.uea.buri.domain.user.User
import br.edu.uea.buri.domain.user.UserAuth
import br.edu.uea.buri.domain.user.UserLogin
import br.edu.uea.buri.domain.user.UserRegister
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface BuriApi {
    @POST("/user")
    suspend fun saveUser(@Body user: UserRegister) : Response<User>
    @POST("/auth/register")
    suspend fun saveUserAuth(@Body user: UserRegister) : Response<User>
    @POST("/auth")
    suspend fun loginUserAuth(@Body user: UserLogin) : Response<UserAuth>
    @GET("/auth/generateId")
    suspend fun generateId() : Response<EquipmentNewId>
    @GET("/user/{userId}")
    suspend fun getUserById(@Path("userId") userId: UUID) : Response<User>
    @DELETE("/user/{userId}")
    suspend fun deleteById(@Path("userId") userId: UUID)

    @GET("/measurement/{measurementId}")
    suspend fun getMeasurementById(@Path("measurementId") measurementId: Long) : Response<Measurement>
    @GET("/measurement/equipment/{id}")
    suspend fun getAllMeasurementsByEquipmentId(@Path("id") equipmentId: String, @Query("page") page: Int, @Query("size") size: Int)

    @POST("/auth/measurement")
    suspend fun saveMeasurementAuth(@Body equipment: EquipmentRegister) : Response<Equipment>
    @GET("/equipment")
    suspend fun getAllEquipmentsByOwnerId(@Query("ownerId") id: UUID) : Response<List<Equipment>>
    @POST("/equipment")
    suspend fun saveEquipment(@Body equipment: Equipment) : Response<Equipment>
    @GET("/equipment/{equipmentId}")
    suspend fun getEquipmentById(@Path("equipmentId") equipmentId: String) : Response<Equipment>
}