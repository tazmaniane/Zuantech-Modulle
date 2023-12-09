package id.zuantech.module.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat

val Throwable.errorMesssage: String
    get() = message ?: "Something went wrong"


fun String.toRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun File.toRequestBody(fieldName: String, mediaType: String = "image/*"): MultipartBody.Part? {
    val reqFile = this.asRequestBody(mediaType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(fieldName, name, reqFile)
}

fun File.toBase64String(): String? {
    try {
        val bitmap = BitmapFactory.decodeFile(this.path)
        val bitmapByte = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapByte)
        return Base64.encodeToString(bitmapByte.toByteArray(), Base64.NO_WRAP) ?: ""
    } catch (e: Exception){
        Log.e("File.toBase64String()", "Exception: ${Gson().toJson(e)}")
        return null
    }
}

fun File.toMegabyte(): Double {
    return ((length() / 1024) / 1024).toDouble()
}

fun File.toSizeMB(): String {
    val sizeMB: Double = ((this.length() / 1024) / 1024).toDouble()
    val format = DecimalFormat("#,###.##")
    return "${format.format(sizeMB)} MB"
}

fun File.toSizeKB(): String {
    val sizeMB: Double = (this.length() / 1024).toDouble()
    val format = DecimalFormat("#,###.##")
    return "${format.format(sizeMB)} KB"
}

fun File.toVideoBase64String(): String? {
    try {
        return Base64.encodeToString(this.readBytes(), Base64.NO_WRAP) ?: ""
    } catch (e: Exception){
        Log.e("File.toBase64String()", "Exception: ${Gson().toJson(e)}")
        return null
    }
}