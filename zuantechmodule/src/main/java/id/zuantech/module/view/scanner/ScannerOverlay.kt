package id.zuantech.module.view.scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import id.zuantech.module.R

class ScannerOverlay : ViewGroup {
  private var left = 0f
  private var top = 0f
  private var endY = 0f
  private var rectWidth = 0
  private var rectHeight = 0
  private var frames = 0
  private var revAnimation = false
  private var lineColor = 0
  private var lineWidth = 0

  constructor(context: Context?) : super(context) {}

  @JvmOverloads
  constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : super(
    context,
    attrs,
    defStyle
  ) {
    val a = context.theme.obtainStyledAttributes(
      attrs,
      R.styleable.ScannerOverlay,
      0, 0
    )
    rectWidth = a.getInteger(
      R.styleable.ScannerOverlay_square_width, resources.getInteger(
        R.integer.scanner_rect_width
      )
    )
    rectHeight = a.getInteger(
      R.styleable.ScannerOverlay_square_height, resources.getInteger(
        R.integer.scanner_rect_height
      )
    )
    lineColor = a.getColor(
      R.styleable.ScannerOverlay_line_color,
      ContextCompat.getColor(context, R.color.scanner_line)
    )
    lineWidth = a.getInteger(
      R.styleable.ScannerOverlay_line_width, resources.getInteger(
        R.integer.line_width
      )
    )
    frames = a.getInteger(
      R.styleable.ScannerOverlay_line_speed, resources.getInteger(
        R.integer.line_width
      )
    )
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
  }

  public override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {}
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    left = ((w - dpToPx(rectWidth)) / 2).toFloat()
    top = ((h - dpToPx(rectHeight)) / 4).toFloat()
    endY = top
    super.onSizeChanged(w, h, oldw, oldh)
  }

  fun dpToPx(dp: Int): Int {
    val displayMetrics = resources.displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
  }

  override fun shouldDelayChildPressedState(): Boolean {
    return false
  }

  override fun dispatchDraw(canvas: Canvas) {
    super.dispatchDraw(canvas)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    // draw transparent rect
    val cornerRadius = 0
    val eraser = Paint()
    eraser.isAntiAlias = true
    eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    val rect = RectF(left, top, dpToPx(rectWidth) + left, dpToPx(rectHeight) + top)
    canvas.drawRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), eraser)

    // draw horizontal line
    val line = Paint()
    line.color = lineColor
    line.strokeWidth = java.lang.Float.valueOf(lineWidth.toFloat())

    //initialize new paint in the constructor
    val borderPaint = Paint()
    borderPaint.isAntiAlias = true
    borderPaint.color = ContextCompat.getColor(context, R.color.white)

    //inside onDraw
    val distance = 60
    val thickness = 12

    //top left corner
    canvas.drawRect(
      left - thickness, top - thickness, distance + left, top, borderPaint
    )
    canvas.drawRect(
      left - thickness, top, left, distance + top, borderPaint
    )

    //top right corner
    canvas.drawRect(
      (dpToPx(rectWidth) + left - distance),
      top - thickness,
      (dpToPx(rectWidth) + left + thickness),
      top,
      borderPaint
    )
    canvas.drawRect(
      dpToPx(rectWidth) + left,
      top,
      (dpToPx(rectWidth) + left + thickness).toFloat(),
      distance + top,
      borderPaint
    )

    //bottom left corner
    canvas.drawRect(
      left - thickness,
      dpToPx(rectHeight) + top, distance + left, (dpToPx(rectHeight) + top + thickness), borderPaint
    )
    canvas.drawRect(
      left - thickness,
      (dpToPx(rectHeight) + top - distance),
      left,
      dpToPx(rectHeight) + top,
      borderPaint
    )

    //bottom right corner
    canvas.drawRect(
      (dpToPx(rectWidth) + left - distance),
      dpToPx(rectHeight) + top,
      (dpToPx(rectWidth) + left + thickness),
      (dpToPx(rectHeight) + top + thickness),
      borderPaint
    )
    canvas.drawRect(
      dpToPx(rectWidth) + left,
      (dpToPx(rectHeight) + top - distance),
      (dpToPx(rectWidth) + left + thickness),
      dpToPx(rectHeight) + top,
      borderPaint
    )

    // draw the line to product animation
    if (endY >= top + dpToPx(rectHeight) + frames) {
      revAnimation = true
    } else if (endY == top + frames) {
      revAnimation = false
    }

    // check if the line has reached to bottom
    if (revAnimation) {
      endY -= frames.toFloat()
    } else {
      endY += frames.toFloat()
    }
    canvas.drawLine(left, endY, left + dpToPx(rectWidth), endY, line)
    invalidate()
  }
}