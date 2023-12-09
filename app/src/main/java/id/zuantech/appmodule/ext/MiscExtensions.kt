package id.zuantech.appmodule.ext

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

val Throwable.errorMesssage: String
    get() = message ?: "0_Something went wrong"


fun String.toRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun File.toRequestBody(fieldName: String, mediaType: String = "image/*"): MultipartBody.Part {
    val reqFile = this.asRequestBody(mediaType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(fieldName, name, reqFile)
}

fun File.toRequestBody(fieldName: String, onProgress: (Int) -> Unit?): MultipartBody.Part {
    val reqFile = this.toRequestBodyWithProgress(("image/*").toMediaTypeOrNull(), onProgress)
    return MultipartBody.Part.createFormData(fieldName, name, reqFile)
}

/** Returns a new request body that transmits the content of this. */
fun File.toRequestBodyWithProgress(contentType: MediaType? = null, onProgress: (Int) -> Unit?): RequestBody {
    return object : RequestBody() {
        override fun contentType() = contentType
        override fun contentLength() = length()
        override fun writeTo(sink: BufferedSink) {
            val fileLength = contentLength()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val inSt = FileInputStream(this@toRequestBodyWithProgress)
            var uploaded = 0L
            inSt.use {
                var read: Int = inSt.read(buffer)
                val handler = Handler(Looper.getMainLooper())
                while (read != -1) {
                    onProgress.let {
                        uploaded += read
                        val progress = (uploaded.toDouble() / (fileLength.toDouble() / 100.0)).toFloat()
                        handler.post { it(progress.toInt()) }
                        sink.write(buffer, 0, read)
                    }
                    read = inSt.read(buffer)
                }
            }
        }
    }
}