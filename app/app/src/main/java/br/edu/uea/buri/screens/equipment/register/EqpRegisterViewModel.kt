package br.edu.uea.buri.screens.equipment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.uea.buri.data.BuriApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EqpRegisterViewModel @Inject constructor(
    private val buriApi: BuriApi
) : ViewModel() {
    private val _id = MutableLiveData<String>("")
    val id : LiveData<String> = _id

    fun generateId() {
        viewModelScope.launch {
            try {
                val response = buriApi.generateId()
                if (response.isSuccessful) {
                    _id.value = response.body()?.id ?: ""
                } else {
                    _id.value = "Erro ao gerar ID: ${response.code()}"
                }
            } catch (e: Exception) {
                _id.value = "Exceção ao gerar ID: ${e.message}"
            }
        }
    }
}