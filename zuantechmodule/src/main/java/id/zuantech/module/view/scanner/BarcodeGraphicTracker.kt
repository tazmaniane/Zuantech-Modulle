package id.zuantech.module.view.scanner

import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode
import id.zuantech.module.view.scanner.camera.GraphicOverlay
import java.util.ArrayList

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
internal class BarcodeGraphicTracker(
  private val mOverlay: GraphicOverlay<BarcodeGraphic>,
  private val mGraphic: BarcodeGraphic,
  private val listener: BarcodeGraphicTrackerListener?
) :
  Tracker<Barcode>() {
  /**
   * Start tracking the detected item instance within the item overlay.
   */
  override fun onNewItem(id: Int, item: Barcode) {
    mGraphic.id = id
    Log.e("XX", "barcode detected: " + item.displayValue + ", listener: " + listener)
    listener?.onScanned(item)
  }

  /**
   * Update the position/characteristics of the item within the overlay.
   */
  override fun onUpdate(detectionResults: Detections<Barcode>, item: Barcode) {
    mOverlay.add(mGraphic)
    mGraphic.updateItem(item)
    if (detectionResults != null && detectionResults.detectedItems.size() > 1) {
      Log.e("XX", "Multiple items detected")
      Log.e("XX", "onUpdate: " + detectionResults.detectedItems.size())
      if (listener != null) {
        val barcodes = asList(detectionResults.detectedItems)
        listener.onScannedMultiple(barcodes)
      }
    }
  }

  /**
   * Hide the graphic when the corresponding object was not detected.  This can happen for
   * intermediate frames temporarily, for example if the object was momentarily blocked from
   * view.
   */
  override fun onMissing(detectionResults: Detections<Barcode>) {
    mOverlay.remove(mGraphic)
  }

  /**
   * Called when the item is assumed to be gone for good. Remove the graphic annotation from
   * the overlay.
   */
  override fun onDone() {
    mOverlay.remove(mGraphic)
  }

  interface BarcodeGraphicTrackerListener {
    fun onScanned(barcode: Barcode?)
    fun onScannedMultiple(barcodes: List<Barcode>?)
    fun onBitmapScanned(sparseArray: SparseArray<Barcode?>?)
    fun onScanError(errorMessage: String?)
  }

  companion object {
    fun <C> asList(sparseArray: SparseArray<C>?): List<C>? {
      if (sparseArray == null) return null
      val arrayList: MutableList<C> = ArrayList(sparseArray.size())
      for (i in 0 until sparseArray.size()) arrayList.add(sparseArray.valueAt(i))
      return arrayList
    }
  }
}