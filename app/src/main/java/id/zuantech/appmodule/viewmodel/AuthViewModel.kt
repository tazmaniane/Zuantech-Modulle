package id.zuantech.appmodule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.zuantech.appmodule.common.ActionLiveData
import id.zuantech.appmodule.common.UiState
import id.zuantech.appmodule.common.storage.Preferences
import id.zuantech.appmodule.ext.errorMesssage
import id.zuantech.appmodule.services.rest.AuthRest
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zuantech.appmodule.common.UiState2
import id.zuantech.appmodule.services.request.RequestLogin
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val preferences: Preferences,
    private val authRest: AuthRest
) : ViewModel() {

    val authState = ActionLiveData<UiState2>()
    val loadState = ActionLiveData<UiState>()

    fun isLoggedIn(): Boolean = preferences.isLoggedIn

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            authState.sendAction(UiState2.Loading)
            try {
                val param = RequestLogin(phone = phone, password = password)
                val response = authRest.login(param)
                if (response.success) {
                    preferences.isLoggedIn = true
                    authState.sendAction(UiState2.Success(response.message))
                } else {
                    if (response.code != null) {
                        authState.sendAction(UiState2.Failed(response.code, response.message))
                    } else {
                        authState.sendAction(UiState2.Error(response.message))
                    }
                }
            } catch (e: Exception) {
                authState.sendAction(UiState2.Error(e.errorMesssage))
            }
        }
    }
}