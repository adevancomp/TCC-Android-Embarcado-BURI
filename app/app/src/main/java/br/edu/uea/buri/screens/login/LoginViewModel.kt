package br.edu.uea.buri.screens.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.domain.user.UserAuth
import br.edu.uea.buri.domain.user.UserLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.http.HTTP
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val api: BuriApi,
    private val shared: SharedPreferences
): ViewModel() {
    private val _state = MutableLiveData<LoginState>()
    val state : LiveData<LoginState> = _state

    fun authenticate(username: String, password: String){
        _state.value = LoginState.Loading
        viewModelScope.launch {
            _state.value = runCatching {
                api.loginUserAuth(UserLogin(username,password))
            }.fold(onSuccess = {  response ->
                when{
                    response.isSuccessful -> {
                        with(shared.edit()){
                            putString("username",username)
                            putString("password",password)
                            response.body()?.id?.let {
                                putString("id",it.toString())
                            }
                            apply()
                        }
                        LoginState.Success(response.body() ?: UserAuth())
                    }
                    response.code() == 403 -> {
                        LoginState.Failed("Usuário ou senha inválida")
                    }
                    else -> {
                        LoginState.Failed("ERRO na API ${response.code()}")
                    }
                }
            }, onFailure = { error ->
                LoginState.Failed(error.message)
            })
        }
    }
}

sealed interface LoginState{
    val auth: UserAuth?
        get() = null
    val isProgressVisible : Boolean
        get() = false

    val errorMessage: String?
        get() = null

    val isErrorMessageVisible: Boolean
        get() = errorMessage != null

    val isAuthenticated: Boolean
        get() = false

    data class Success(override val auth: UserAuth, override val isAuthenticated: Boolean = true) : LoginState

    object Loading: LoginState {
        override val isProgressVisible: Boolean = true
    }

    data class Failed(override val errorMessage: String?) : LoginState
}