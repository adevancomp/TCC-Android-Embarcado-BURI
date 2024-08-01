package br.edu.uea.buri.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _authenticated = MutableLiveData<Boolean>(false)
    val authenticated : LiveData<Boolean> = _authenticated
    fun authenticate(){
        _authenticated.value= true
    }
    fun logout (){
        _authenticated.value= false
    }
}