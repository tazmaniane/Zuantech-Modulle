package id.zuantech.module.view.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import id.zuantech.module.view.scanner.camera.CameraSource
import id.zuantech.module.view.scanner.camera.CameraSourcePreview
import id.zuantech.module.view.scanner.camera.GraphicOverlay
import id.zuantech.module.R
import java.io.IOException

class BarcodeReader : Fragment(), OnTouchListener,
  BarcodeGraphicTracker.BarcodeGraphicTrackerListener {
  // constants used to pass extra data in the intent
  private var autoFocus = false
  private var useFlash = false
  private var beepSoundFile: String? = null
  private var isPaused = false
  private var mCameraSource: CameraSource? = null
  private var mPreview: CameraSourcePreview? = null
  private var mGraphicOverlay: GraphicOverlay<BarcodeGraphic>? = null

  // helper objects for detecting taps and pinches.
  private var scaleGestureDetector: ScaleGestureDetector? = null
  private var gestureDetector: GestureDetector? = null
  private var mListener: BarcodeReaderListener? = null
  private var permissionStatus: SharedPreferences? = null
  private var sentToSettings = false
  fun setListener(barcodeReaderListener: BarcodeReaderListener?) {
    mListener = barcodeReaderListener
  }

  fun setBeepSoundFile(fileName: String?) {
    beepSoundFile = fileName
  }

  fun pauseScanning() {
    isPaused = true
  }

  fun resumeScanning() {
    isPaused = false
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.barcode_reader_view, container, false)
    permissionStatus = activity?.getSharedPreferences("permissionStatus", Context.MODE_PRIVATE)
    mPreview = view.findViewById(R.id.preview)
    mGraphicOverlay = view.findViewById(R.id.graphicOverlay)
    gestureDetector = GestureDetector(activity, CaptureGestureListener())
    scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())
    view.setOnTouchListener(this)
    return view
  }

  override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
    super.onInflate(context, attrs, savedInstanceState)
    val a = context.obtainStyledAttributes(attrs, R.styleable.BarcodeReader)
    autoFocus = a.getBoolean(R.styleable.BarcodeReader_auto_focus, true)
    useFlash = a.getBoolean(R.styleable.BarcodeReader_use_flash, false)
    a.recycle()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is BarcodeReaderListener) {
      mListener = context
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    if (ContextCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.CAMERA
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        requireActivity(), arrayOf(Manifest.permission.CAMERA), PERMISSION_CALLBACK_CONSTANT
      )
    } else {
      proceedAfterPermission()
    }
  }

  private fun proceedAfterPermission() {
    createCameraSource(autoFocus, useFlash)
  }

  fun setUseFlash(useflash: Boolean) : BarcodeReader {
    this.useFlash = useflash
    return this
  }

  fun useflash(useflash: Boolean) {
    mCameraSource?.setFlashMode(if (useflash) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF)
  }

  /**
   * Creates and starts the camera.  Note that this uses a higher resolution in comparison to other
   * detection examples to enable the barcode detector to detect small barcodes at long distances.
   *
   *
   * Suppressing InlinedApi since there is a check that the minimum version is met before using the
   * constant.
   */
  @SuppressLint("InlinedApi") private fun createCameraSource(
    autoFocus: Boolean,
    useFlash: Boolean
  ) {
    Log.e(TAG, "createCameraSource:")
    val context: Context? = activity

    // A barcode detector is created to track barcodes.  An associated multi-processor instance
    // is set to receive the barcode detection results, track the barcodes, and maintain
    // graphics for each barcode on screen.  The factory is used by the multi-processor to
    // create a separate tracker instance for each barcode.
    val barcodeDetector = BarcodeDetector.Builder(context).build()
    val barcodeFactory = mGraphicOverlay?.let { BarcodeTrackerFactory(it, this) }
    barcodeDetector.setProcessor(
      MultiProcessor.Builder(barcodeFactory).build()
    )
    if (!barcodeDetector.isOperational) {
      // Note: The first time that an app using the barcode or face API is installed on a
      // device, GMS will download a native libraries to the device in order to do detection.
      // Usually this completes before the app is run for the first time.  But if that
      // download has not yet completed, then the above call will not detect any barcodes
      // and/or faces.
      //
      // isOperational() can be used to check if the required native libraries are currently
      // available.  The detectors will automatically become operational once the library
      // downloads complete on device.
      Log.w(TAG, "Detector dependencies are not yet available.")

      // Check for low storage.  If there is low storage, the native library will not be
      // downloaded, so detection will not become operational.
      val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
      val hasLowStorage = activity?.registerReceiver(null, lowstorageFilter) != null
      if (hasLowStorage) {
        Toast.makeText(
          activity, R.string.low_storage_error,
          Toast.LENGTH_LONG
        ).show()
        Log.w(TAG, getString(R.string.low_storage_error))
      }
    }

    // Creates and starts the camera.  Note that this uses a higher resolution in comparison
    // to other detection examples to enable the barcode detector to detect small barcodes
    // at long distances.
    var builder = CameraSource.Builder(activity, barcodeDetector)
      .setFacing(CameraSource.CAMERA_FACING_BACK)
    // .setRequestedPreviewSize(width, height)

    // make sure that auto focus is an available option
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      builder = builder.setFocusMode(
        if (autoFocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE else null
      )
    }
    mCameraSource = builder
      .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else null)
      .build()
  }

  /**
   * Restarts the camera.
   */
  override fun onResume() {
    super.onResume()
    startCameraSource()
    if (sentToSettings) {
      if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED
      ) {
        //Got Permission
        proceedAfterPermission()
      } else {
        mListener?.onCameraPermissionDenied()
      }
    }
  }

  /**
   * Stops the camera.
   */
  override fun onPause() {
    super.onPause()
    if (mPreview != null) {
      mPreview?.stop()
    }
  }

  /**
   * Releases the resources associated with the camera source, the associated detectors, and the
   * rest of the processing pipeline.
   */
  override fun onDestroy() {
    super.onDestroy()
    // if (mPreview != null) {
    //   mPreview?.release()
    // }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int, permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
      //check if all permissions are granted
      var allgranted = false
      for (i in grantResults.indices) {
        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
          allgranted = true
        } else {
          allgranted = false
          break
        }
      }
      if (allgranted) {
        proceedAfterPermission()
      } else if (ActivityCompat.shouldShowRequestPermissionRationale(
          requireActivity(),
          Manifest.permission.CAMERA
        )
      ) {
        val builder = AlertDialog.Builder(
          activity
        )
        builder.setTitle(getString(R.string.grant_permission))
        builder.setMessage(getString(R.string.permission_camera))
        builder.setPositiveButton(
          R.string.grant
        ) { dialog, which ->
          dialog.cancel()
          requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_CALLBACK_CONSTANT
          )
        }
        builder.setNegativeButton(
          R.string.cancel
        ) { dialog, which ->
          dialog.cancel()
          mListener?.onCameraPermissionDenied()
        }
        builder.show()
      } else {
        mListener?.onCameraPermissionDenied()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_PERMISSION_SETTING) {
      if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED
      ) {
        //Got Permission
        proceedAfterPermission()
      }
    }
  }

  /**
   * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  @Throws(SecurityException::class) private fun startCameraSource() {
    // check that the device has play services available.
    val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
      requireContext()
    )
    if (code != ConnectionResult.SUCCESS) {
      val dlg = GoogleApiAvailability.getInstance().getErrorDialog(
        requireActivity(), code, RC_HANDLE_GMS
      )
      dlg?.show()
    }
    if (mCameraSource != null) {
      try {
        mPreview?.start(mCameraSource, mGraphicOverlay)
      } catch (e: IOException) {
        Log.e(TAG, "Unable to start camera source.", e)
        mCameraSource?.release()
        mCameraSource = null
      }
    }
  }

  /**
   * onTap returns the tapped barcode result to the calling Activity.
   *
   * @param rawX - the raw position of the tap
   * @param rawY - the raw position of the tap.
   * @return true if the activity is ending.
   */
  private fun onTap(rawX: Float, rawY: Float): Boolean {
    // Find tap point in preview frame coordinates.
    val location = IntArray(2)
    mGraphicOverlay?.getLocationOnScreen(location)
    val x = (rawX - location[0]) / mGraphicOverlay!!.widthScaleFactor
    val y = (rawY - location[1]) / mGraphicOverlay!!.heightScaleFactor

    // Find the barcode whose center is closest to the tapped point.
    var best: Barcode? = null
    var bestDistance = Float.MAX_VALUE
    val graphics = mGraphicOverlay?.mGraphics
    graphics?.map { graphic ->
      val barcode = graphic.barcode
      if (barcode?.boundingBox?.contains(x.toInt(), y.toInt()) == true) {
        // Exact hit, no need to keep looking.
        best = barcode
        // break
      }
      barcode?.boundingBox?.let {
        val dx = x - it.centerX()
        val dy = y - it.centerY()
        val distance = dx * dx + dy * dy // actually squared distance
        if (distance < bestDistance) {
          best = barcode
          bestDistance = distance
        }
      }
    }

    if (best != null) {
      val data = Intent()
      data.putExtra(BarcodeObject, best)

      // TODO - pass the scanned value
      activity?.setResult(CommonStatusCodes.SUCCESS, data)
      activity?.finish()
      return true
    }
    return false
  }

  override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
    val b = scaleGestureDetector?.onTouchEvent(motionEvent)
    val c = gestureDetector?.onTouchEvent(motionEvent)
    return b == true || c == true || view.onTouchEvent(motionEvent)
  }

  override fun onScanned(barcode: Barcode?) {
    if (mListener != null && !isPaused) {
      mListener?.onScanned(barcode)
    }
  }

  override fun onScannedMultiple(barcodes: List<Barcode>?) {
    if (mListener != null && !isPaused) {
      mListener?.onScannedMultiple(barcodes)
    }
  }

  override fun onBitmapScanned(sparseArray: SparseArray<Barcode?>?) {
    if (mListener != null) {
      mListener?.onBitmapScanned(sparseArray)
    }
  }

  override fun onScanError(errorMessage: String?) {
    if (mListener != null) {
      mListener?.onScanError(errorMessage)
    }
  }

  private inner class CaptureGestureListener : SimpleOnGestureListener() {
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
      return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
    }
  }

  private inner class ScaleListener : OnScaleGestureListener {
    /**
     * Responds to scaling events for a gesture in progress. Reported by pointer motion.
     *
     * @param detector The detector reporting the event - use this to retrieve extended info about
     * event state.
     * @return Whether or not the detector should consider this event as handled. If an event was
     * not handled, the detector will continue to accumulate movement until an event is handled.
     * This can be useful if an application, for example, only wants to update scaling factors if
     * the change is greater than 0.01.
     */
    override fun onScale(detector: ScaleGestureDetector): Boolean {
      return false
    }

    /**
     * Responds to the beginning of a scaling gesture. Reported by new pointers going down.
     *
     * @param detector The detector reporting the event - use this to retrieve extended info about
     * event state.
     * @return Whether or not the detector should continue recognizing this gesture. For example, if
     * a gesture is beginning with a focal point outside of a region where it makes sense,
     * onScaleBegin() may return false to ignore the rest of the gesture.
     */
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
      return true
    }

    /**
     * Responds to the end of a scale gesture. Reported by existing pointers going up.
     *
     *
     * Once a scale has ended, [ScaleGestureDetector.getFocusX] and [ ][ScaleGestureDetector.getFocusY] will return focal point of the pointers remaining on the
     * screen.
     *
     * @param detector The detector reporting the event - use this to retrieve extended info about
     * event state.
     */
    override fun onScaleEnd(detector: ScaleGestureDetector) {
      mCameraSource?.doZoom(detector.scaleFactor)
    }
  }

  fun playBeep() {
    var m = MediaPlayer()
    try {
      if (m.isPlaying) {
        m.stop()
        m.release()
        m = MediaPlayer()
      }
      val descriptor =
        activity?.assets?.openFd(((if (beepSoundFile != null) beepSoundFile else "mp3/beep.mp3").toString()))
      descriptor?.startOffset?.let {
        m.setDataSource(
          descriptor?.fileDescriptor, it,
          descriptor?.length
        )
      }
      descriptor?.close()
      m.prepare()
      m.setVolume(1f, 1f)
      m.start()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  interface BarcodeReaderListener {
    fun onScanned(barcode: Barcode?)
    fun onScannedMultiple(barcodes: List<Barcode?>?)
    fun onBitmapScanned(sparseArray: SparseArray<Barcode?>?)
    fun onScanError(errorMessage: String?)
    fun onCameraPermissionDenied()
  }

  companion object {
    private val TAG = BarcodeReader::class.java.simpleName

    // intent request code to handle updating play services if needed.
    private const val RC_HANDLE_GMS = 9001
    const val BarcodeObject = "Barcode"
    private const val PERMISSION_CALLBACK_CONSTANT = 101
    private const val REQUEST_PERMISSION_SETTING = 102
  }
}