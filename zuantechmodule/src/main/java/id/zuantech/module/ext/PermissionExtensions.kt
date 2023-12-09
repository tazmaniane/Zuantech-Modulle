package id.zuantech.module.ext

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.projeku.tvvoucher.view.dialog.PopupPermissionGuide

const val REQUEST_CODE_PERMISSIONS = 11101
const val REQUEST_CODE_PERMISSIONS_MAIN_ACTIVITY = 111011
const val REQUEST_CODE_CAMERA_ETC_PERMISSIONS = 11102

const val CAMERA_PERMISSION = Manifest.permission.CAMERA
const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS
//const val BLUETOOTH = Manifest.permission.BLUETOOTH
//const val BLUETOOTH_SCAN = Manifest.permission.BLUETOOTH_SCAN
//const val BLUETOOTH_CONNECT = Manifest.permission.BLUETOOTH_CONNECT
//const val BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN
const val RECORD_AUDIO = Manifest.permission.RECORD_AUDIO
const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
const val READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
const val WRITE_INTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

fun Activity.requestPermissions() {
    val permissions = mutableListOf<String>()
    if (!checkSinglePermission(POST_NOTIFICATIONS)) {
        permissions.add(POST_NOTIFICATIONS)
    }
    if (!checkSinglePermission(ACCESS_FINE_LOCATION)) {
        permissions.add(ACCESS_FINE_LOCATION)
    }
    if (!checkSinglePermission(ACCESS_COARSE_LOCATION)) {
        permissions.add(ACCESS_COARSE_LOCATION)
    }
//    if (!checkSinglePermission(RECORD_AUDIO)) {
//        permissions.add(RECORD_AUDIO)
//    }
//    if (!checkSinglePermission(RECORD_AUDIO)) {
//        permissions.add(RECORD_AUDIO)
//    }
//    if (!checkSinglePermission(this, BLUETOOTH)) {
//        permissions.add(BLUETOOTH)
//    }
//    if (!checkSinglePermission(this, BLUETOOTH_SCAN)) {
//        permissions.add(BLUETOOTH_SCAN)
//    }
//    if (!checkSinglePermission(this, BLUETOOTH_CONNECT)) {
//        permissions.add(BLUETOOTH_CONNECT)
//    }
//    if (!checkSinglePermission(this, BLUETOOTH_ADMIN)) {
//        permissions.add(BLUETOOTH_ADMIN)
//    }
//    if (!checkSinglePermission(READ_EXTERNAL_STORAGE)) {
//        permissions.add(READ_EXTERNAL_STORAGE)
//    }
//    if (!checkSinglePermission(WRITE_INTERNAL_STORAGE)) {
//        permissions.add(WRITE_INTERNAL_STORAGE)
//    }
    if (permissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_CODE_PERMISSIONS)
    }
}

fun Context.checkSinglePermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.checkMultiplePermission(permission: List<String>) : Boolean {
    for(i in permission.indices){
        if(!checkSinglePermission(permission[i])) return false
    }
    return true
}


interface PermissionListener {
    fun onGranted(onGranted: Boolean)
}

fun Context.showPermissionGuide(message: String){
    PopupPermissionGuide(this)
        .popupMessage(message)
        .onClickPositive { (this as Activity?)?.gotoAppSetting() }
        .show()
}

const val PERMISSION_MAIN_ACTIVITY_DENIED_MESSAGE = "Izin Lokasi & Notifikasi perangkat dibutuhkan agar aplikasi dapat digunakan"
fun Activity.requestPermissionsMainActivity(permissionListener: PermissionListener?) {
    val permissions = mutableListOf<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if(!checkSinglePermission(POST_NOTIFICATIONS)) permissions.add(POST_NOTIFICATIONS)
    }
    if(!checkSinglePermission(ACCESS_COARSE_LOCATION)) permissions.add(ACCESS_COARSE_LOCATION)
    if(!checkSinglePermission(ACCESS_FINE_LOCATION)) permissions.add(ACCESS_FINE_LOCATION)
    val allPermissionGranted = checkMultiplePermission(permissions)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        when {
            allPermissionGranted -> {
                permissionListener?.onGranted(true)
            }
            shouldShowRequestPermissionRationale(POST_NOTIFICATIONS) ||
                    shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION) ||
                    shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)
            -> { showPermissionGuide(PERMISSION_MAIN_ACTIVITY_DENIED_MESSAGE) }
            else -> {
                requestPermissions(permissions.toTypedArray(), REQUEST_CODE_PERMISSIONS_MAIN_ACTIVITY)
            }
        }
    } else {
        when {
            allPermissionGranted -> {
                permissionListener?.onGranted(true)
            }
            else -> {
                requestPermissions(permissions.toTypedArray(), REQUEST_CODE_PERMISSIONS_MAIN_ACTIVITY)
            }
        }
    }
}

fun Activity.onPermissionResult(
    requestCodeActivity: Int, requestCode: Int, permissions: Array<out String>,
    permissionListener: PermissionListener?, permissionDeniedMessage: String
) {
    when(requestCodeActivity) {
        requestCode -> {
            val allPermissionIsGranted = checkMultiplePermission(permissions.toList())
            if(allPermissionIsGranted) {
                permissionListener?.onGranted(true)
            } else {
                showPermissionGuide(permissionDeniedMessage)
            }
            return
        }
        else -> { }
    }
}