package br.edu.uea.buri.screens.register

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.domain.user.UserRegister
import br.edu.uea.buri.screens.login.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRegisterViewModel @Inject constructor(
    private val api: BuriApi,
    private val shared: SharedPreferences
) : ViewModel() {
    private val _state = MutableLiveData<RegisterState>()
    val state : LiveData<RegisterState> = _state

    fun register(name: String, email: String, password: String, role: String){
        val user = UserRegister(
            name = name,
            email = email,
            password = password,
            role = role
        )

        _state.value = RegisterState.Loading
        viewModelScope.launch {
            _state.value = runCatching {
                api.saveUserAuth(user)
            }.fold(
                onSuccess = {
                    response ->
                        when{
                            response.isSuccessful -> {
                                with(shared.edit()){
                                    putString("username",response.body()?.name)
                                    putString("password",user.password)
                                    response.body()?.userId?.let {
                                        putString("id",it.toString())
                                    }
                                    apply()
                                }
                                RegisterState.Success(user,true)
                            }
                            else -> {
                                RegisterState.Failed(response.errorBody().toString())
                            }
                        }
                }, onFailure = { error ->
                    RegisterState.Failed(error.message ?: "Erro no cadastro da API")
                }
            )
        }
    }
}

sealed interface RegisterState {
    val user: UserRegister?
        get() = null
    val isProgressVisible: Boolean
        get() = false
    val errorMessage: String?
        get() = null
    val isErrorMessageVisible: Boolean
        get() = errorMessage != null
    val isUserSaved: Boolean
        get() = false

    data class Success(override val user: UserRegister, override val isUserSaved: Boolean = true) : RegisterState
    object Loading: RegisterState{
        override val isProgressVisible: Boolean = true
    }
    data class Failed(override val errorMessage : String) : RegisterState
}