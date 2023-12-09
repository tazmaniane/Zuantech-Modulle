package id.zuantech.appmodule

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ZuantechModuleApp : Application(){

    companion object {
        val TAG = ZuantechModuleApp::class.java.simpleName
        lateinit var instance: ZuantechModuleApp
            private set
        fun newInstance() = ZuantechModuleApp()
    }

    override fun onCreate() {
        super.onCreate()
        instance = newInstance()
    }
}