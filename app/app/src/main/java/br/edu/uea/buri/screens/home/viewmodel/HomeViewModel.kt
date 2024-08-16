package br.edu.uea.buri.screens.home.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.data.database.dao.EquipmentDao
import br.edu.uea.buri.data.database.dao.UserDao
import br.edu.uea.buri.data.database.entity.EquipmentEntity
import br.edu.uea.buri.data.database.entity.UserEntity
import br.edu.uea.buri.domain.equipment.Equipment
import br.edu.uea.buri.domain.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: BuriApi,
    private val shared: SharedPreferences,
    private val userDao: UserDao,
    private val equipmentDao: EquipmentDao
): ViewModel() {
    private val _homeState = MutableLiveData<HomeState>(HomeState.EmptyState)
    val homeState : LiveData<HomeState> = _homeState

    init {
        CoroutineScope(Dispatchers.IO).launch {

            if (!shared.getString("id", "").isNullOrEmpty()) {
                val response: Response<User> = api.getUserById(UUID.fromString(shared.getString("id", "")))
                if(response.isSuccessful){
                    response.body()?.let {
                        userDao.deleteById(it.userId)
                        userDao.insertWithoutEquipments(
                            UserEntity(
                                id = it.userId,
                                email = it.email
                            )
                        )
                        val responseEquipmentsList: Response<List<Equipment>> = api.getAllEquipmentsByOwnerId(it.userId)
                        if(responseEquipmentsList.isSuccessful){
                            responseEquipmentsList.body()?.let { list ->
                                list.forEach { equipment ->
                                    equipmentDao.insert(
                                        EquipmentEntity(
                                            id = equipment.id,
                                            name = equipment.name,
                                            userId = equipment.ownerId!!
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

    fun fetchData(){
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            _homeState.value = runCatching {
                api.getAllEquipmentsByOwnerId(UUID.fromString(shared.getString("id","")))
            }.fold(
                onSuccess = { response ->
                    when{
                        response.isSuccessful -> {
                            HomeState.Success(response.body() ?: emptyList())
                        }
                        response.code() == 403 -> {
                            HomeState.Failed("Usuário ou senha inválida")
                        }
                        else -> {
                            HomeState.Failed("Erro na API ${response.code()}")
                        }
                    }
                }, onFailure = {
                    error ->
                        HomeState.Failed(error.message)
                }
            )
        }
    }
}

sealed interface HomeState {
    val isProgressVisible: Boolean
        get() = false
    val listEquipments : List<Equipment>
        get() = emptyList()
    val errorMessage: String?
        get() = null
    val isErrorMessageVisible: Boolean
        get() = errorMessage!= null
    val isMessageEmptyListVisible: Boolean
        get() = listEquipments.isEmpty()

    data class Success(override val listEquipments: List<Equipment>) : HomeState
    data object EmptyState : HomeState{
        override val listEquipments = emptyList<Equipment>()
    }
    data object Loading: HomeState {
        override val isProgressVisible: Boolean = true
    }
    data class Failed(override val errorMessage: String?) : HomeState
}