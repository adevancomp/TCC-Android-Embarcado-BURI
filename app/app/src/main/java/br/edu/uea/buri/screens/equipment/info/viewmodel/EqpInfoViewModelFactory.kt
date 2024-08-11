package br.edu.uea.buri.screens.equipment.info.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.edu.uea.buri.data.BuriApi

class EqpInfoViewModelFactory (
    private val buriApi: BuriApi,
    private val equipmentId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass == EqpInfoViewModel::class.java){
            return EqpInfoViewModel(buriApi,equipmentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel instance for $modelClass")
    }
}