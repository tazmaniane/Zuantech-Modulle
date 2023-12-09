package id.zuantech.appmodule.ext

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val REQUEST_CODE_PERMISSIONS = 11101
const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS
const val ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE
//const val CAMERA_PERMISSION = Manifest.permission.CAMERA
//const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
//const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
//const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
//const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

fun AppCompatActivity.requestPermissions() {
    val permissions = mutableListOf<String>()
    if (!checkSinglePermission(this, POST_NOTIFICATIONS)) {
        permissions.add(POST_NOTIFICATIONS)
    }
    if (!checkSinglePermission(this, ACCESS_NETWORK_STATE)) {
        permissions.add(ACCESS_NETWORK_STATE)
    }
//    if (!checkSinglePermission(this, CAMERA_PERMISSION)) {
//        permissions.add(CAMERA_PERMISSION)
//    }
//    if (!checkSinglePermission(this, READ_EXTERNAL_STORAGE)) {
//        permissions.add(READ_EXTERNAL_STORAGE)
//    }
//    if (!checkSinglePermission(this, WRITE_EXTERNAL_STORAGE)) {
//        permissions.add(WRITE_EXTERNAL_STORAGE)
//    }
//    if (!checkSinglePermission(this, ACCESS_FINE_LOCATION)) {
//        permissions.add(ACCESS_FINE_LOCATION)
//    }
//    if (!checkSinglePermission(this, ACCESS_COARSE_LOCATION)) {
//        permissions.add(ACCESS_COARSE_LOCATION)
//    }
    if (permissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(
            this, permissions.toTypedArray(), REQUEST_CODE_PERMISSIONS
        )
    }
}

private fun checkSinglePermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}