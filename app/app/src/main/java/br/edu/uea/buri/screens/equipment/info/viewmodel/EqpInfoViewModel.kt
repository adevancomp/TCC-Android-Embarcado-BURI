package br.edu.uea.buri.screens.equipment.info.viewmodel

import android.icu.text.IDNA.Info
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.data.pages.MeasurementPage
import br.edu.uea.buri.domain.equipment.Equipment
import br.edu.uea.buri.domain.measurement.Measurement
import br.edu.uea.buri.screens.home.repository.BluetoothEsp32Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class EqpInfoViewModel (
    private val buriApi: BuriApi,
    private val equipmentId: String,
    private val buriBluetoothRepo: BluetoothEsp32Repository
) : ViewModel() {

    private val _state = MutableStateFlow<InfoState>(InfoState.Loading)
    val state : StateFlow<InfoState> = _state

    init {
        viewModelScope.launch {
            while (true){
                val measurement: Measurement? = fetchLastMeasurement()
                measurement?.let {
                    _state.value  = InfoState.Success(it, _state.value.isOnline)
                    delay(60000)
                }
            }
        }
    }

    private suspend fun fetchLastMeasurement() : Measurement?{
        var measurement: Measurement? = null
        /*val pageResponse: Response<MeasurementPage> =
            buriApi.getAllMeasurementsByEquipmentId(equipmentId, 0, 1)
        if(pageResponse.isSuccessful){
            measurement = pageResponse.body()?.measurements?.firstOrNull()
        } else {
            _state.value = InfoState.Failed(pageResponse.code().toString(),_state.value.isOnline)
        }*/
        return measurement
    }

    fun changeForOnline(){
        _state.value = InfoState.PersonLoading(isProgressVisible = true, isOnline = true)
    }

    fun changeForOffline(){
        _state.value = InfoState.PersonLoading(isProgressVisible = true, isOnline = false)
    }

    sealed interface InfoState {
        val measurement: Measurement?
            get() = null
        val isProgressVisible : Boolean
            get() = false
        val errorMessage: String?
            get() = null
        val isErrorMessageVisible: Boolean
            get() = errorMessage != null

        val isOnline: Boolean
            get() = true

        data class Success(override val measurement: Measurement, override val isOnline: Boolean) : InfoState
        data class Failed(override val errorMessage: String, override val isOnline: Boolean) : InfoState
        data class PersonLoading(override val isProgressVisible: Boolean, override val isOnline: Boolean) : InfoState
        data object Loading : InfoState{
            override val isProgressVisible: Boolean = true
        }
    }
}