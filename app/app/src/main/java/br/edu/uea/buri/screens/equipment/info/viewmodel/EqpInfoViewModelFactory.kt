package br.edu.uea.buri.screens.equipment.info.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.screens.home.repository.BluetoothEsp32Repository

class EqpInfoViewModelFactory (
    private val buriApi: BuriApi,
    private val equipmentId: String,
    private val buriBluetoothRepo: BluetoothEsp32Repository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass == EqpInfoViewModel::class.java){
            return EqpInfoViewModel(buriApi,equipmentId, buriBluetoothRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel instance for $modelClass")
    }
}