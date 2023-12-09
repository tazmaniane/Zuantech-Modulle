package id.zuantech.module.view.scanner.camera

import android.Manifest
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import java.io.IOException

class CameraSourcePreview(
  private val mContext: Context, attrs: AttributeSet?
) : ViewGroup(mContext, attrs) {
  private val mSurfaceView: SurfaceView
  private var mStartRequested = false
  private var mSurfaceAvailable = false
  private var mCameraSource: CameraSource? = null
  private var mOverlay: GraphicOverlay<*>? = null

  @Throws(IOException::class, SecurityException::class) fun start(cameraSource: CameraSource?) {
    if (cameraSource == null) {
      stop()
    }
    mCameraSource = cameraSource
    if (mCameraSource != null) {
      mStartRequested = true
      startIfReady()
    }
  }

  @Throws(IOException::class, SecurityException::class) fun start(
    cameraSource: CameraSource?,
    overlay: GraphicOverlay<*>?
  ) {
    mOverlay = overlay
    start(cameraSource)
  }

  fun stop() {
    if (mCameraSource != null) {
      mCameraSource!!.stop()
    }
  }

  fun release() {
    if (mCameraSource != null) {
      mCameraSource!!.release()
      mCameraSource = null
    }
  }

  @Throws(IOException::class, SecurityException::class)
  private fun startIfReady() {
    if (mStartRequested && mSurfaceAvailable) {
      mCameraSource?.start(mSurfaceView.holder)
      if (mOverlay != null) {
        val size = mCameraSource?.previewSize
        val min = size?.let { Math.min(it.width, size.height) }
        val max = size?.let { Math.max(it.width, size.height) }
        // Swap width and height sizes when in portrait, since it will be rotated by
        // 90 degrees
        if (min != null) {
          if (max != null) {
            mOverlay!!.setCameraInfo(min, max, mCameraSource!!.cameraFacing)
          }
        }
        mOverlay!!.clear()
      }
      mStartRequested = false
    }
  }

  private inner class SurfaceCallback : SurfaceHolder.Callback {
    override fun surfaceCreated(surface: SurfaceHolder) {
      mSurfaceAvailable = true
      try {
        startIfReady()
      } catch (se: SecurityException) {
        Log.e(TAG, "Do not have permission to start the camera", se)
      } catch (e: IOException) {
        Log.e(TAG, "Could not start camera source.", e)
      }
    }

    override fun surfaceDestroyed(surface: SurfaceHolder) {
      mSurfaceAvailable = false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    var width = width
    var height = height
    if (mCameraSource != null) {
      val size = mCameraSource!!.previewSize
      if (size != null) {
        width = size.width
        height = size.height
      }
    }
    val layoutWidth = right - left
    val layoutHeight = bottom - top

    // val tmp = width
    // width = height
    // height = tmp

    // val childHeight = layoutHeight
    // val childWidth = (layoutHeight.toFloat() / height.toFloat() * width).toInt()

    for (i in 0 until childCount) {
      getChildAt(i).layout(0, 0, layoutWidth, layoutHeight)
    }
    try {
      startIfReady()
    } catch (se: SecurityException) {
      Log.e(TAG, "Do not have permission to start the camera", se)
    } catch (e: IOException) {
      Log.e(TAG, "Could not start camera source.", e)
    }
  }

  /*@Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      int width = 320;
      int height = 240;
      if (mCameraSource != null) {
          Size size = mCameraSource.getPreviewSize();
          if (size != null) {
              width = size.getWidth();
              height = size.getHeight();
          }
      }

      // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
      if (isPortraitMode()) {
          int tmp = width;
          //noinspection SuspiciousNameCombination
          width = height;
          height = tmp;
      }

      final int layoutWidth = right - left;
      final int layoutHeight = bottom - top;

      // Computes height and width for potentially doing fit width.
      int childWidth = layoutWidth;
      int childHeight = (int) (((float) layoutWidth / (float) width) * height);

      // If height is too tall using fit width, does fit height instead.
      if (childHeight > layoutHeight) {
          childHeight = layoutHeight;
          childWidth = (int) (((float) layoutHeight / (float) height) * width);
      }

      for (int i = 0; i < getChildCount(); ++i) {
          getChildAt(i).layout(0, 0, childWidth, childHeight);
      }

      try {
          startIfReady();
      } catch (SecurityException se) {
          Log.e(TAG, "Do not have permission to start the camera", se);
      } catch (IOException e) {
          Log.e(TAG, "Could not start camera source.", e);
      }
  }*/

  companion object {
    private const val TAG = "CameraSourcePreview"
  }

  init {
    mSurfaceView = SurfaceView(mContext)
    mSurfaceView.holder.addCallback(SurfaceCallback())
    addView(mSurfaceView)
  }
}