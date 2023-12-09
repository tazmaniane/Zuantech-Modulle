package id.zuantech.appmodule.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import id.zuantech.appmodule.common.storage.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val preferences: Preferences,
    val application: Application,
) : ViewModel() {

    val isLoggedIn = preferences.isLoggedIn

    fun setLoggedIn(isLoggedIn: Boolean) {
        preferences.isLoggedIn = isLoggedIn
    }

    fun logout() = preferences.clear()

}