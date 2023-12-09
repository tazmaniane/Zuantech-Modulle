package id.zuantech.appmodule.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.gson.Gson
import id.zuantech.appmodule.R
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.setColor(@ColorRes colorRes: Int) {
    return this.setBackgroundColor(ContextCompat.getColor(context, colorRes))
}
fun TextView.setColor(@ColorRes colorRes: Int) {
    return this.setTextColor(ContextCompat.getColor(context, colorRes))
}
fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = resources.getColor(R.color.primary_base)
                textPaint.isUnderlineText = false
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}


fun String.toHtml(): String {
   return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        "${Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)}"
    } else {
        "${Html.fromHtml(this)}"
    }
}


fun File.toSizeMB(): String {
    val sizeMB: Double = (this.length() / 1000000).toDouble()
    val format = DecimalFormat("#,###.##")
    return "${format.format(sizeMB)}MB"
}

fun File.toSizeKB(): String {
    val sizeMB: Double = (this.length() / 1000).toDouble()
    val format = DecimalFormat("#,###.##")
    return "${format.format(sizeMB)}KB"
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun String.scanTextToNumber(): String {
    return filterNot {
        it.isWhitespace()
    }.uppercase()
        .replace("O", "0")
        .replace("@", "0")
        .replace("!", "1")
        .replace("|", "1")
        .replace("T", "1")
        .replace("I", "1")
        .replace("Z", "2")
        .replace("B", "8")
        .replace("A", "4")
        .replace("S", "5")
        .replace("$", "5")
        .replace("G", "6")
        .replace("/", "7")
}


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun TextView.currency(number: Double){
    val formatter = DecimalFormat("#,###")
    this.setText("Rp ${formatter.format(number)}")
}

fun Date?.diffMinute(): Long {
    if (this != null) {
        val diff: Long = Date().time - this.time
        val seconds = diff / 1000
        val minutes = seconds / 60
//        val hours = minutes / 60
//        val days = hours / 24
        return minutes.toLong()
    } else {
        return 0
    }
}

fun Date.toYYYYMMDD():String{
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(this)
}

fun getDateTimeNow():String{
    val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm aa", Locale.getDefault())
    val now = Date()
    return sdf.format(now)
}

fun getDateNow():String{
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val now = Date()
    return sdf.format(now)
}

fun getTimeNow():String{
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val now = Date()
    return sdf.format(now)
}

fun Double.toNumberLocation(): String {
    val formatter = DecimalFormat("#,###.#####")
    return formatter.format(this)
}

fun Double.toCurrencyNoSymbol(currency: String = ""): String {
    val formatter = NumberFormat.getCurrencyInstance() as DecimalFormat
    with(formatter) {
        decimalFormatSymbols = decimalFormatSymbols.apply {
            currencySymbol = currency
        }
        maximumFractionDigits = 0
    }
    return formatter.format(this)
}

fun getRupiah(number: Double): String{
    val localeID =  Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    return numberFormat.format(number).toString()
}

@SuppressLint("SimpleDateFormat")
fun getConvertDate(start: String, end: String, nilai: String):String{

    val fmt = SimpleDateFormat(start)
    val d = fmt.parse(nilai)
    val format = SimpleDateFormat(end)

    return format.format(d!!)
}

@SuppressLint("SimpleDateFormat")
fun getConvertTime(start: String, end: String, nilai: String): String{

    val fmt2 = SimpleDateFormat(start)
    val d2 = fmt2.parse(nilai)
    val format2 = SimpleDateFormat(end)

    return format2.format(d2!!)
}

@SuppressLint("SimpleDateFormat")
fun getformatDateTime(orderTime: String, format: String): String{
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val outputFormat = SimpleDateFormat(format)

    var formattedDate=""
    if(format == "HH:mm"){
        val date = inputFormat.parse(orderTime)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.HOUR, 7)
        formattedDate = outputFormat.format(calendar.time)
    }else {
        val date = inputFormat.parse(orderTime)
        formattedDate = outputFormat.format(date)
    }
    return formattedDate
}

@SuppressLint("SimpleDateFormat")
fun getformatDateTimePayment(orderTime: String, format: String): String{
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val outputFormat = SimpleDateFormat(format)
    val date = inputFormat.parse(orderTime)
    val formattedDate = outputFormat.format(date)
//    println(formattedDate) // prints 10-04-2018

    return formattedDate
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun NavController.safeNavigate(direction: NavDirections) {
//    Log.d(clickTag, "Click happened")
    currentDestination?.getAction(direction.actionId)?.run {
//        Log.d(clickTag, "Click Propagated")
        navigate(direction)
    }
}

fun View.blockingClickListener(debounceTime: Long = 1200L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            val timeNow = SystemClock.elapsedRealtime()
            val elapsedTimeSinceLastClick = timeNow - lastClickTime
//            Log.d(clickTag, """
//                        DebounceTime: $debounceTime
//                        Time Elapsed: $elapsedTimeSinceLastClick
//                        Is within debounce time: ${elapsedTimeSinceLastClick < debounceTime}
//                    """.trimIndent())

            if (elapsedTimeSinceLastClick < debounceTime) {
//                Log.d(clickTag, "Double click shielded")
                return
            }
            else {
//                Log.d(clickTag, "Click happened")
                action()
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun Long.toCurrency(currency: String = "", locale: Locale = Locale("id")): String {
    val formatter = NumberFormat.getCurrencyInstance(locale) as DecimalFormat
    with(formatter) {
        decimalFormatSymbols = decimalFormatSymbols.apply {
            currencySymbol = currency
        }
        maximumFractionDigits = 0
    }
    return "Rp ${formatter.format(this)}"
}

fun Long.toCurrencyNoSymbol(currency: String = ""): String {
    val formatter = NumberFormat.getCurrencyInstance() as DecimalFormat
    with(formatter) {
        decimalFormatSymbols = decimalFormatSymbols.apply {
            currencySymbol = currency
        }
        maximumFractionDigits = 0
    }
    return formatter.format(this)
}

fun String.toRupiah(currency: String = "0"): String {
    val check = this.indexOf('.')
    return if (check < 0) this.toLong().toCurrency() else this.substring(0, check).toLong().toCurrency()
}

fun Long.toRupiah(): String {
    return this.toCurrency()
}

fun String.toRemoveCurrency(): String {
    var str = this.replace(".", "").replace("Rp", "").trim()
    if(str.isEmpty()) return "0"
    if (str.contains(",")) {
        val strs: List<String> = str.split(",")
        str = strs[0].replace(".", "")
    }
    return str
}

fun View.getBitmap(): Bitmap? {
//    val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//    measure(spec, spec)
//    layout(0, 0, measuredWidth, measuredHeight)
//    val b = Bitmap.createBitmap(measuredWidth, measuredWidth, Bitmap.Config.ARGB_8888)
    layout(0, 0, width, height)
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    draw(c)
    return b
}


fun Context.appInstalledOrNot(packageName: String): Boolean {
    val pm: PackageManager = packageManager
    val app_installed: Boolean = try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return app_installed
}


fun Context.getAppVersionName() : String {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    } catch (e: Exception) {
        "0.0.0"
    }
}

fun Context.getAppVersionCode() : Int?{
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionCode
    } catch (e: Exception) {
        0
    }
}

fun Context.appIsExpired(versionCode: Int) : Boolean {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val currentVersion = pInfo.versionCode
        currentVersion < versionCode
    } catch (e: Exception) {
        false
    }
}

@Throws(IOException::class)
fun Context.readJSONDataFromFile(rawId: Int): String? {
    var inputStream: InputStream? = null
    val builder = StringBuilder()
    try {
        var jsonString: String? = null
        inputStream = resources.openRawResource(rawId)
        val bufferedReader = BufferedReader(
            InputStreamReader(inputStream, "UTF-8")
        )
        while (bufferedReader.readLine().also { jsonString = it } != null) {
            builder.append(jsonString)
        }
    } finally {
        inputStream?.close()
    }
    val result = String(builder)
    return result
}


fun Context.openWebPage(url: String) {
    val bundle = Bundle()
    bundle.putBoolean("new_window", true) //sets new window
    try {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        intent.putExtras(bundle)
        startActivity(intent)
    } catch (e: java.lang.Exception) {
        Log.e("ERROR #1", "ERROR: " + Gson().toJson(e))
        try {
//                Uri webpage = Uri.parse(url);
            val webpage = Uri.parse(url.replace("https", "http"))
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            intent.putExtras(bundle)
            startActivity(intent)
        } catch (e2: java.lang.Exception) {
            Log.e("ERROR #2", "ERROR: " + Gson().toJson(e2))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(Intent.createChooser(intent, "Choose browser"))
        }
    }
}

@Throws(Throwable::class)
fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
    var bitmap: Bitmap? = null
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(videoPath, HashMap())
        //   mediaMetadataRetriever.setDataSource(videoPath);
        bitmap = mediaMetadataRetriever.frameAtTime
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
    } finally {
        mediaMetadataRetriever?.release()
    }
    return bitmap
}

fun String.toMD5(): String {
    try {
        // Create MD5 Hash
        val digest: MessageDigest = MessageDigest
            .getInstance("MD5")
        digest.update(toByteArray())
        val messageDigest: ByteArray = digest.digest()

        // Create Hex String
        val hexString = StringBuffer()
        for (i in messageDigest.indices) {
            var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    } catch (e: NoSuchAlgorithmException) {
//        Logger.logStackTrace(TAG, e)
    }
    return ""
}
