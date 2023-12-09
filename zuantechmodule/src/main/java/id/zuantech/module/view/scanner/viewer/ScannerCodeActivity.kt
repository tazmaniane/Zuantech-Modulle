package id.zuantech.module.view.scanner.viewer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.gms.vision.barcode.Barcode
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import id.projeku.tvvoucher.view.dialog.PopupPermissionGuide
import id.zuantech.module.R
import id.zuantech.module.base.BaseActivity
import id.zuantech.module.databinding.BarcodeQrcodeScannerActivityBinding
import id.zuantech.module.ext.*
import java.io.File
import java.io.IOException


class ScannerCodeActivity : BaseActivity(), ScannerCodeFragmentListener {

    companion object {
        val TAG = ScannerCodeActivity::class.java.simpleName
        val RESULT_SCANNER_CODE = "resultscannercode"
        const val REQUEST_CODE_PERMISSIONS = 1000
        const val REQUEST_CODE_PERMISSIONS_CAMERA = 2000
    }

    private val mBinding by lazy { BarcodeQrcodeScannerActivityBinding.inflate(layoutInflater) }

    private val mPickerImageActivity = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriFilePath = result.getUriFilePath(this)
            val path = uriFilePath.toString()
            addImageFileToView(path = path)
        } else {
            val message = result.error?.message ?: result.error?.localizedMessage ?: "Telah terjadi kesalahan"
            toast(message)
        }
    }

    private var mCurrentFragment: Fragment? = null
    private var mScannerFragment: ScannerCodeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        val permissionNeeded = mutableListOf<String>()
        if(!checkSinglePermission(CAMERA_PERMISSION)) permissionNeeded.add(CAMERA_PERMISSION)
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
            if(!checkSinglePermission(READ_EXTERNAL_STORAGE)) permissionNeeded.add(READ_EXTERNAL_STORAGE)
            if(!checkSinglePermission(WRITE_INTERNAL_STORAGE)) permissionNeeded.add(WRITE_INTERNAL_STORAGE)
        }

        val permissionsGranted = checkMultiplePermission(permissionNeeded)
        when {
            permissionsGranted -> { }
            shouldShowRequestPermissionRationale(CAMERA_PERMISSION) ||
                    shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(WRITE_INTERNAL_STORAGE)
            -> {
                PopupPermissionGuide(this)
                    .popupMessage("Beberapa izin akses ke perangkat diperlukan, agar proses dapat dilanjutkan")
                    .onClickPositive { gotoAppSetting() }
                    .show()
            }
            else -> {
                requestPermissions(permissionNeeded.toTypedArray(), REQUEST_CODE_PERMISSIONS_CAMERA)
            }
        }

        initView()
    }


    private fun initView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.navigationBarColor = ContextCompat.getColor(this, R.color.grey_lighter)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        mScannerFragment = ScannerCodeFragment.newInstance()
        mCurrentFragment = mScannerFragment
        addFragment(mScannerFragment)
        showFragment(mScannerFragment)

        mBinding.cardFlash.setOnClickListener {
            mScannerFragment?.flashlightToggle()
            mBinding.tvFlashlight.text = if(mScannerFragment?.useflash == true){
                mBinding.ivFlashlight.setImageResource(R.drawable.ic_flashlight_on)
                "Flash ON"
            } else {
                mBinding.ivFlashlight.setImageResource(R.drawable.ic_flashlight_off)
                "Flash OFF"
            }
        }

        mBinding.cardGallery.setOnClickListener {
            startPickImageFromGallery()
        }
    }

    private fun addFragment(fragment: Fragment?) {
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, it, fragment::class.java.simpleName)
                .hide(it)
                .commit()
        }
    }

    private fun showFragment(fragment: Fragment?) {
        fragment?.let {
            mCurrentFragment?.let { it1 ->
                supportFragmentManager.beginTransaction()
                    .hide(it1)
                    .show(it)
                    .commit()
            }
            mCurrentFragment = it
        }
    }


    private fun sendResult(data: String){
        val intent = Intent()
        intent.putExtra(RESULT_SCANNER_CODE, data)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun addImageFileToView(path: String){
        val file: File? = File(path)
        if(file?.exists() == true) {
            try {
                val image = InputImage.fromFilePath(this, Uri.fromFile(file))
                val scanner = BarcodeScanning.getClient()
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        Log.w(TAG, Gson().toJson(barcodes))
                        if(barcodes.isEmpty()){
                            mScannerFragment?.playBeep()
                            toast("Kode tidak ditemukan")
                            return@addOnSuccessListener
                        }
                        for (barcode in barcodes) {
//                            val bounds = barcode.boundingBox
//                            val corners = barcode.cornerPoints
//                            val valueType = barcode.valueType
                            val rawValue = barcode.rawValue
                            if(!rawValue.isNullOrEmpty() && !this@ScannerCodeActivity.isFinishing){
                                mScannerFragment?.playBeep()
                                sendResult(rawValue.toString())
                                return@addOnSuccessListener
                            } else {
                                if(barcode == barcodes.last()) {
                                    mScannerFragment?.playBeep()
                                    toast("Kode tidak ditemukan")
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, it.errorMesssage)
                        mScannerFragment?.playBeep()
                        toast(it.errorMesssage)
                    }
            } catch (e: IOException) {
                mScannerFragment?.playBeep()
                Log.e(TAG, e.errorMesssage)
                toast(e.errorMesssage)
            }
        }
    }

    private fun startPickImageFromGallery() {
        val permissionNeeded = mutableListOf<String>()
        if(!checkSinglePermission(CAMERA_PERMISSION)) permissionNeeded.add(CAMERA_PERMISSION)
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
            if(!checkSinglePermission(READ_EXTERNAL_STORAGE)) permissionNeeded.add(READ_EXTERNAL_STORAGE)
            if(!checkSinglePermission(WRITE_INTERNAL_STORAGE)) permissionNeeded.add(WRITE_INTERNAL_STORAGE)
        }

        val permissionsGranted = checkMultiplePermission(permissionNeeded)
        when {
            permissionsGranted -> {
                mPickerImageActivity.launch(CropImageContractOptions(null, getCropImageOptions(gallery = true, camera = false)))
            }
            shouldShowRequestPermissionRationale(CAMERA_PERMISSION) ||
                    shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(WRITE_INTERNAL_STORAGE)
            -> {
                PopupPermissionGuide(this)
                    .popupMessage("Beberapa izin akses ke perangkat diperlukan, agar proses dapat dilanjutkan")
                    .onClickPositive { gotoAppSetting() }
                    .show()
            }
            else -> {
                requestPermissions(permissionNeeded.toTypedArray(), REQUEST_CODE_PERMISSIONS)
            }
        }
    }

    override fun fragmentCreated(fragment: Fragment) {
    }

    override fun onScanned(barcode: Barcode?) {
        Log.d(TAG, "barcode.displayValue: ${barcode?.displayValue}")
        sendResult(barcode?.displayValue ?: "")
    }

    private fun Context.getCropImageOptions(gallery: Boolean, camera: Boolean) : CropImageOptions {
        return CropImageOptions().apply {
            imageSourceIncludeGallery = gallery
            imageSourceIncludeCamera = camera
            autoZoomEnabled = false
            guidelines = CropImageView.Guidelines.ON
            cropShape = CropImageView.CropShape.RECTANGLE
            allowFlipping = false
            allowRotation = true
            initialRotation = 0
            cropMenuCropButtonTitle = "Lanjutkan"
            outputCompressQuality = 100
            outputCompressFormat = Bitmap.CompressFormat.JPEG
        }
    }

    private val permissionCamera by lazy {
        object : PermissionListener {
            override fun onGranted(onGranted: Boolean) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    private val permissionCameraAndStorageListener by lazy {
        object : PermissionListener {
            override fun onGranted(onGranted: Boolean) {
                Log.w(TAG, "onGranted: $onGranted")
                startPickImageFromGallery()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.w("onRequestPermissionsResult", requestCode.toString())
        when(requestCode){
            REQUEST_CODE_PERMISSIONS -> {
                onPermissionResult(
                    requestCodeActivity = requestCode,
                    requestCode = REQUEST_CODE_PERMISSIONS,
                    permissions = permissions,
                    permissionListener = permissionCameraAndStorageListener,
                    permissionDeniedMessage = "Izin Kamera & Penyimpanan dibutuhkan agar aplikasi dapat digunakan"
                )
            }
            REQUEST_CODE_PERMISSIONS_CAMERA -> {
                onPermissionResult(
                    requestCodeActivity = requestCode,
                    requestCode = REQUEST_CODE_PERMISSIONS_CAMERA,
                    permissions = permissions,
                    permissionListener = permissionCamera,
                    permissionDeniedMessage = "Izin Kamera dibutuhkan agar aplikasi dapat digunakan"
                )
            }
        }
    }
}