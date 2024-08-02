package br.edu.uea.buri.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: ConnectivityRepository
) : ViewModel() {
    private val _authenticated = MutableLiveData<Boolean>(false)
    val authenticated : LiveData<Boolean> = _authenticated
    val isConnected: LiveData<Boolean> = repo.isConnected.asLiveData()
    private val _buriWifiConnection = MutableLiveData<Boolean>(false)
    val buriWifiConnection : LiveData<Boolean> = _buriWifiConnection
    fun authenticate(){
        _authenticated.value= true
    }
    fun logout (){
        _authenticated.value= false
    }
}