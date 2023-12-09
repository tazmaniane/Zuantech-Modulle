package id.zuantech.appmodule.common.storage

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(private val prefs: SharedPreferences) {

    var isLoggedIn: Boolean by PreferenceData(prefs, "isLoggedIn", false)
    var token: String by PreferenceData(prefs, "token", "")
    var lastOpenAdTime: Long by PreferenceData(prefs, "lastOpenAdTime", 0)

    fun clear() {
        prefs.edit().clear().apply()
    }
}