package id.zuantech.module.view.scanner

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.android.gms.vision.barcode.Barcode
import id.zuantech.module.view.scanner.camera.GraphicOverlay

/**
 * Graphic instance for rendering barcode position, size, and ID within an associated graphic
 * overlay view.
 */
class BarcodeGraphic internal constructor(overlay: GraphicOverlay<*>) :
  GraphicOverlay.Graphic(overlay) {
  var id = 0
  private val mRectPaint: Paint
  private val mTextPaint: Paint
  @Volatile var barcode: Barcode? = null
    private set

  /**
   * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
   * relevant portions of the overlay to trigger a redraw.
   */
  fun updateItem(barcode: Barcode?) {
    this.barcode = barcode
    postInvalidate()
  }

  /**
   * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
   */
  override fun draw(canvas: Canvas?) {
    val barcode = barcode ?: return

    // Draws the bounding box around the barcode.
    val rect = RectF(barcode.boundingBox)
    rect.left = translateX(rect.left)
    rect.top = translateY(rect.top)
    rect.right = translateX(rect.right)
    rect.bottom = translateY(rect.bottom)
    canvas?.drawRect(rect, mRectPaint)

    // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
    canvas?.drawText(barcode.rawValue, rect.left, rect.bottom, mTextPaint)
  }

  companion object {
    private val COLOR_CHOICES = intArrayOf(
      Color.BLUE,
      Color.CYAN,
      Color.GREEN
    )
    private var mCurrentColorIndex = 0
  }

  init {
    mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.size
    val selectedColor = COLOR_CHOICES[mCurrentColorIndex]
    mRectPaint = Paint()
    mRectPaint.color = selectedColor
    mRectPaint.style = Paint.Style.STROKE
    mRectPaint.strokeWidth = 4.0f
    mTextPaint = Paint()
    mTextPaint.color = selectedColor
    mTextPaint.textSize = 36.0f
  }
}