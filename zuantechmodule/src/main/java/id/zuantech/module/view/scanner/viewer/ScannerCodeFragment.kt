package id.zuantech.module.view.scanner.viewer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.barcode.Barcode
import id.zuantech.module.R
import id.zuantech.module.view.scanner.BarcodeReader

interface ScannerCodeFragmentListener {
    fun fragmentCreated(fragment: Fragment)
    fun onScanned(barcode: Barcode?)
}

class ScannerCodeFragment : Fragment(),
    BarcodeReader.BarcodeReaderListener {

    companion object {
        val TAG = ScannerCodeFragment::class.java.simpleName
        fun newInstance() = ScannerCodeFragment()
    }

    private lateinit var barcodeReader: BarcodeReader
    var useflash = false

    private var mScannerCodeFragmentListener: ScannerCodeFragmentListener? = null
    fun setScannerCodeFragmentListener(listener: ScannerCodeFragmentListener): ScannerCodeFragment {
        this.mScannerCodeFragmentListener = listener
        return this
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")
        super.onAttach(context)
        mScannerCodeFragmentListener = if (context is ScannerCodeFragmentListener) {
            context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        mScannerCodeFragmentListener = null
        super.onDetach()
    }

    private fun bindView(view: View) {
        barcodeReader = childFragmentManager.findFragmentById(R.id.barcode_fragment) as BarcodeReader
        barcodeReader.setUseFlash(useflash).setListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.barcode_qrcode_scanner_fragment, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mScannerCodeFragmentListener?.fragmentCreated(this)
        bindView(view)
    }

    fun flashlightToggle(): ScannerCodeFragment {
        useflash = !useflash
        Log.w(TAG, "useflash: $useflash")
        barcodeReader.useflash(useflash)
        return this
    }

    fun playBeep(): ScannerCodeFragment {
        barcodeReader.playBeep()
        return this
    }

    override fun onHiddenChanged(hidden: Boolean) {
        Log.d(TAG, "hidden: $hidden")
        super.onHiddenChanged(hidden)
        if (hidden) {
            barcodeReader.pauseScanning()
        } else {
            barcodeReader.resumeScanning()
        }
    }

    override fun onScanned(barcode: Barcode?) {
        barcodeReader.playBeep()
        mScannerCodeFragmentListener?.onScanned(barcode)
    }

    override fun onScannedMultiple(barcodes: List<Barcode?>?) {
        Log.d(TAG, "onScannedMultiple: ${barcodes?.size}")
        var codes = ""
        for (barcode in barcodes!!) {
            codes += barcode?.displayValue + ", "
        }
        val finalCodes = codes
        Log.d(TAG, "finalCodes: $finalCodes")
    }

    override fun onBitmapScanned(sparseArray: SparseArray<Barcode?>?) {
    }

    override fun onScanError(errorMessage: String?) {
        Log.e(TAG, "onScanError: $errorMessage")
//        showToast(errorMessage ?: "onScanError")
    }

    override fun onCameraPermissionDenied() {
//        SnackbarUtils.show(
//            requireActivity(),
//            "Kamera tidak di-izinkan, Silakan cek permission aplikasi Mitra anda di menu Pengaturan.",
//            false,
//            null
//        )
    }
}