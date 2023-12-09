package id.zuantech.appmodule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.zuantech.appmodule.common.ActionLiveData
import id.zuantech.appmodule.common.UiState
import id.zuantech.appmodule.common.storage.Preferences
import id.zuantech.appmodule.services.rest.MainRest
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zuantech.appmodule.ZuantechModuleApp
import id.zuantech.appmodule.ext.errorMesssage
import id.zuantech.appmodule.services.entity.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferences: Preferences,
    private val mainRest: MainRest
) : ViewModel() {

    val loadState = ActionLiveData<UiState>()
    val sendState = ActionLiveData<UiState>()

    val responseKUHPItems = ActionLiveData<List<ResponseKUHPItem>>()
    val responseKUHAPItems = ActionLiveData<List<ResponseKUHAPItem>>()
    val responseKUHPerdataItems = ActionLiveData<List<ResponseKUHPerdataItem>>()

    val responseKUHPDetail = ActionLiveData<ResponseKUHPDetail>()
    val responseKUHAPDetail = ActionLiveData<ResponseKUHAPDetail>()
    val responseKUHPerdataDetail = ActionLiveData<ResponseKUHPerdataDetail>()

    fun kuhp() {
        viewModelScope.launch {
            loadState.sendAction(UiState.Loading)
            try {
                val response = ZuantechModuleApp.dbHelper.kuhp()
                responseKUHPItems.value = response
                loadState.sendAction(UiState.Success)
            } catch (e: Exception){
                loadState.sendAction(UiState.Error(e.errorMesssage))
            }
        }
    }

    fun kuhap() {
        viewModelScope.launch {
            loadState.sendAction(UiState.Loading)
            try {
                val response = ZuantechModuleApp.dbHelper.kuhap()
                responseKUHAPItems.value = response
                loadState.sendAction(UiState.Success)
            } catch (e: Exception){
                loadState.sendAction(UiState.Error(e.errorMesssage))
            }
        }
    }

    fun kuhperdata() {
        viewModelScope.launch {
            loadState.sendAction(UiState.Loading)
            try {
                val response = ZuantechModuleApp.dbHelper.kuhperdata()
                responseKUHPerdataItems.value = response
                loadState.sendAction(UiState.Success)
            } catch (e: Exception){
                loadState.sendAction(UiState.Error(e.errorMesssage))
            }
        }
    }

//    fun kuhpsearch(search: String) {
//        viewModelScope.launch {
//            loadState.sendAction(UiState.Loading)
//            try {
//                val response = if(search.isNullOrEmpty()){
//                    ZuantechModuleApp.dbHelper.kuhp()
//                } else {
//                    ZuantechModuleApp.dbHelper.kuhpsearch(search = search)
//                }
//                responseKUHPItems.value = response
//                loadState.sendAction(UiState.Success)
//            } catch (e: Exception){
//                loadState.sendAction(UiState.Error(e.errorMesssage))
//            }
//        }
//    }
//
//    fun kuhapsearch(search: String) {
//        viewModelScope.launch {
//            loadState.sendAction(UiState.Loading)
//            try {
//                val response = if(search.isNullOrEmpty()){
//                    ZuantechModuleApp.dbHelper.kuhap()
//                } else {
//                    ZuantechModuleApp.dbHelper.kuhapsearch(search = search)
//                }
//                responseKUHAPItems.value = response
//                loadState.sendAction(UiState.Success)
//            } catch (e: Exception){
//                loadState.sendAction(UiState.Error(e.errorMesssage))
//            }
//        }
//    }
//
//    fun kuhperdatasearch(search: String) {
//        viewModelScope.launch {
//            loadState.sendAction(UiState.Loading)
//            try {
//                val response = if(search.isNullOrEmpty()){
//                    ZuantechModuleApp.dbHelper.kuhperdata()
//                } else {
//                    ZuantechModuleApp.dbHelper.kuhperdatasearch(search = search)
//                }
//                responseKUHPerdataItems.value = response
//                loadState.sendAction(UiState.Success)
//            } catch (e: Exception){
//                loadState.sendAction(UiState.Error(e.errorMesssage))
//            }
//        }
//    }

    fun kuhpdetail(id: String) {
        viewModelScope.launch {
            loadState.sendAction(UiState.Loading)
            try {
                val response = ZuantechModuleApp.dbHelper.kuhpdetail(id = id)
                responseKUHPDetail.value = response
                loadState.sendAction(UiState.Success)
            } catch (e: Exception){
                loadState.sendAction(UiState.Error(e.errorMesssage))
            }
        }
    }

    fun kuhapdetail(id: String) {
        viewModelScope.launch {
            loadState.sendAction(UiState.Loading)
            try {
                val response = ZuantechModuleApp.dbHelper.kuhapdetail(id = id)
                responseKUHAPDetail.value = response
                loadState.sendAction(UiState.Success)
            } catch (e: Exception){
                loadState.sendAction(UiState.Error(e.errorMesssage))
            }
        }
    }

    fun kuhperdatadetail(id: String) {
        viewModelScope.launch {
            loadState.sendAction(UiState.Loading)
            try {
                val response = ZuantechModuleApp.dbHelper.kuhperdatadetail(id = id)
                responseKUHPerdataDetail.value = response
                loadState.sendAction(UiState.Success)
            } catch (e: Exception){
                loadState.sendAction(UiState.Error(e.errorMesssage))
            }
        }
    }
}