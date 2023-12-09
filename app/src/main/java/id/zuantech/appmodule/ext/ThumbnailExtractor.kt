package id.zuantech.appmodule.ext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import java.io.IOException


interface ThumbnailListener {
    fun onLoading(isLoading: Boolean)
    fun onSuccess(bitmap: Bitmap?)
    fun onFailed(message: String?)
}

@SuppressLint("StaticFieldLeak")
class ThumbnailExtractor(private val context: Context) : AsyncTask<String?, Void?, Bitmap?>() {

    private var thumbnailListener: ThumbnailListener? = null

    fun setThumbnailListener(thumbnailListener: ThumbnailListener): ThumbnailExtractor {
        this.thumbnailListener = thumbnailListener
        return this
    }

    override fun onPreExecute() {
        super.onPreExecute()
        thumbnailListener?.onLoading(true)
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: String?): Bitmap? {
        val videoPath = params[0]
        var thumbnailBitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, Uri.parse(videoPath))
//            retriever.setDataSource(mediaSource)

//            retriever.setDataSource(Uri.parse(videoPath))
            //   mediaMetadataRetriever.setDataSource(videoPath);
            thumbnailBitmap = retriever.getFrameAtTime(1, 10)
        } catch (e: Exception) {
            e.printStackTrace()
            thumbnailListener?.onLoading(false)
        } finally {
            try {
                retriever.release()
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
            thumbnailListener?.onLoading(false)
        }
        return thumbnailBitmap
    }

    override fun onPostExecute(result: Bitmap?) {
        thumbnailListener?.onSuccess(result)
        thumbnailListener?.onLoading(false)
    }
}