package br.edu.uea.buri.screens.equipment.register

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.data.database.dao.UserDao
import br.edu.uea.buri.data.database.entity.EquipmentEntity
import br.edu.uea.buri.domain.equipment.Equipment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EqpRegisterViewModel @Inject constructor(
    private val buriApi: BuriApi,
    private val shared: SharedPreferences,
    private val userDao: UserDao
) : ViewModel() {

    private val _state = MutableLiveData<EqpRegisterState>(EqpRegisterState.Default)
    val state : LiveData<EqpRegisterState> = _state

    fun generateId() {
        viewModelScope.launch {
            _state.value = runCatching {
                buriApi.generateId()
            }.fold(
                onSuccess = { response ->
                    when{
                        response.isSuccessful ->
                            EqpRegisterState.SetNewEquipment(response.body()?.id ?: "")
                        else -> EqpRegisterState.Failed( "Erro na api ${response.code()}")
                    }
                },
                onFailure = {
                    error ->
                        EqpRegisterState.Failed(error.message ?: "ERRO NA API")
                }
            )
        }
    }

    fun chanceOwner() {
        _state.value = EqpRegisterState.ChangeOwner
    }

    fun saveEquipment(equipment: Equipment) {
        viewModelScope.launch {
            _state.value = runCatching {
                buriApi.saveEquipment(equipment)
            }.fold(
                onSuccess = {
                    response ->
                        when{
                            response.isSuccessful ->{
                                userDao.insertEquipment(
                                    EquipmentEntity(
                                        id = equipment.id,
                                        name = equipment.name,
                                        userId = equipment.ownerId!!
                                    )
                                )
                                EqpRegisterState.Success(equipment)}
                            else->
                                EqpRegisterState.Failed("Erro na API ${response.code()}")
                        }
                },
                onFailure = { error ->
                    EqpRegisterState.Failed(error.message ?: "ERRO")
                }
            )
        }
    }

    suspend fun searchEquipmentById(equipmentId: String): Equipment? {
        return try {
            withContext(Dispatchers.IO) {
                val response = buriApi.getEquipmentById(equipmentId)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    sealed interface EqpRegisterState {
        // Mostra elementos do cadastro novo equipamento
        // Mostra elementos da mudança de dono
        // Padrão: deixa esses elementos escondidos
        val equipment: Equipment?
            get() = null
        val elementsRegisterNewEquipmentVisible: Boolean
            get() = false
        val elementsRegisterChangeOwnerVisible: Boolean
            get() = false
        val id : String
            get() = equipment?.id ?: ""
        val defaultMode: Boolean
            get() = true
        val errorMessage: String?
            get() = null
        val isErrorMessageVisible: Boolean
            get() = errorMessage != null

        data class Success(override val equipment: Equipment, override val defaultMode : Boolean = true,
                           override val elementsRegisterNewEquipmentVisible: Boolean = false,override val elementsRegisterChangeOwnerVisible: Boolean = false) : EqpRegisterState
        data class SetNewEquipment(override val id: String, override val elementsRegisterNewEquipmentVisible: Boolean = true) : EqpRegisterState
        data class Failed(override val errorMessage: String) : EqpRegisterState
        data object NewEquipment : EqpRegisterState {
            override val defaultMode: Boolean = false
            override val elementsRegisterNewEquipmentVisible: Boolean = true
            override val elementsRegisterChangeOwnerVisible: Boolean = false
        }
        data object ChangeOwner : EqpRegisterState {
            override val defaultMode: Boolean = false
            override val elementsRegisterNewEquipmentVisible: Boolean = false
            override val elementsRegisterChangeOwnerVisible: Boolean = true
        }
        data object Default : EqpRegisterState{
            override val defaultMode: Boolean = true
            override val elementsRegisterNewEquipmentVisible: Boolean = false
            override val elementsRegisterChangeOwnerVisible: Boolean = false
        }
    }
}