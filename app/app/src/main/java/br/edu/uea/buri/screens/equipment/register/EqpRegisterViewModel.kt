package br.edu.uea.buri.screens.equipment.register

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import br.edu.uea.buri.data.BuriApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EqpRegisterViewModel @Inject constructor(
    private val buriApi: BuriApi,
    private val shared: SharedPreferences
) : ViewModel() {

}