package br.edu.uea.buri.screens.equipment.info.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.domain.measurement.Measurement

class EqpInfoViewModel (
    private val buriApi: BuriApi,
    private val equipmentId: String
) : ViewModel() {

    sealed interface InfoState {
        val measurement: Measurement?
            get() = null
        val isProgressVisible : Boolean
            get() = false
        val errorMessage: String?
            get() = null
        val isErrorMessageVisible: Boolean
            get() = errorMessage != null
    }
}