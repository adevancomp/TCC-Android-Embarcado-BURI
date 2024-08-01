package br.edu.uea.buri.screens.home.viewmodel

import android.content.SharedPreferences
import android.media.audiofx.DynamicsProcessing.Eq
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.domain.equipment.Equipment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: BuriApi,
    private val shared: SharedPreferences
): ViewModel() {
    private val _homeState = MutableLiveData<HomeState>(HomeState.EmptyState)
    val homeState : LiveData<HomeState> = _homeState

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