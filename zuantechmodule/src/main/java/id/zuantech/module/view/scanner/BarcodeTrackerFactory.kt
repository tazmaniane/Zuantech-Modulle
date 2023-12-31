package id.zuantech.module.view.scanner

import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode
import id.zuantech.module.view.scanner.camera.GraphicOverlay


/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
internal class BarcodeTrackerFactory(
  private val mGraphicOverlay: GraphicOverlay<BarcodeGraphic>,
  private val listener: BarcodeGraphicTracker.BarcodeGraphicTrackerListener
) :
  MultiProcessor.Factory<Barcode> {
  override fun create(barcode: Barcode): Tracker<Barcode> {
    val graphic = BarcodeGraphic(mGraphicOverlay)
    return BarcodeGraphicTracker(mGraphicOverlay, graphic, listener)
  }
}