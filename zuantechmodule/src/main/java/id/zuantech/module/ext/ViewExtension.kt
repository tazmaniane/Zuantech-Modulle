package id.zuantech.module.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
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
import com.google.gson.Gson
import id.zuantech.module.R
import java.io.*
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

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
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

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun TextView.currency(number: Double){
    val formatter = DecimalFormat("#,###")
    this.setText("Rp ${formatter.format(number)}")
}

fun getDateNow():String{
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val now = Date()
    return sdf.format(now)
}

fun String.toDateParse(format: String) : Date? {
    val format = SimpleDateFormat(format, Locale.getDefault())
    return try {
        format.parse(this)
    } catch (e: Exception){
        null
    }
}

fun Date?.toDDMMMMYYYYHHMM():String{
    val sdf = SimpleDateFormat("dd MMMM yyyy hh:mm", Locale.getDefault())
    return if(this != null){
        sdf.format(this)
    } else {
        ""
    }
}

fun Date?.toDDMMMMYYYY():String{
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return if(this != null){
        sdf.format(this)
    } else {
        ""
    }
}

fun Date?.toDDMMMYYYY():String{
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return if(this != null){
        sdf.format(this)
    } else {
        ""
    }
}

fun Date?.toDDMMYYYY():String{
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return if(this != null){
        sdf.format(this)
    } else {
        ""
    }
}

fun Date?.toYYYYMMDD():String{
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return if(this != null){
        sdf.format(this)
    } else {
        ""
    }
}

fun Date.isSameDay(date: Date) : Boolean {
    val calendarFrom = Calendar.getInstance()
    val calendarTo = Calendar.getInstance()
    calendarFrom.time = this
    calendarTo.time = date
    val isSameDay = calendarFrom[Calendar.DAY_OF_YEAR] == calendarTo[Calendar.DAY_OF_YEAR] &&
            calendarFrom[Calendar.MONTH] == calendarTo[Calendar.MONTH] &&
            calendarFrom[Calendar.YEAR] == calendarTo[Calendar.YEAR]
    return isSameDay
}

fun getRupiah(number: Double): String{
    val localeID =  Locale("in", "ID")
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    return numberFormat.format(number).toString()
}

fun String.toDoubleNumber() : Double{
    if(this.isNullOrEmpty()){
        return 0.0
    } else {
        return this.trim()
            .replace("Rp.", "")
            .replace("Rp", "")
            .replace(".", "")
            .replace(",", "")
            .toDouble()
    }
}

fun String.toStringNumber() : String{
    return this.trim()
        .replace("Rp.", "")
        .replace("Rp", "")
        .replace(".", "")
        .replace(",", "")
}

fun Double.toStringNumber() : String{
    return this.toString().toStringNumber()
}

fun Double.toRupiah(): String{
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")) as DecimalFormat
    with(formatter) {
        decimalFormatSymbols = decimalFormatSymbols.apply {
            currencySymbol = ""
        }
        maximumFractionDigits = 0
    }
    return formatter.format(this).toString()
}

fun Long.toRupiah(): String{
    return this.toDouble().toRupiah()
}


@SuppressLint("SimpleDateFormat")
fun getConvertDate(start: String, end: String, nilai: String):String{

    val fmt = SimpleDateFormat(start, Locale.getDefault())
    val d = fmt.parse(nilai)
    val format = SimpleDateFormat(end, Locale.getDefault())

    return format.format(d!!)
}

@SuppressLint("SimpleDateFormat")
fun getConvertTime(start: String, end: String, nilai: String): String{

    val fmt2 = SimpleDateFormat(start, Locale.getDefault())
    val d2 = fmt2.parse(nilai)
    val format2 = SimpleDateFormat(end, Locale.getDefault())

    return format2.format(d2!!)
}

@SuppressLint("SimpleDateFormat")
fun getformatDateTime(orderTime: String, format: String): String{
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat(format, Locale.getDefault())

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
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat(format, Locale.getDefault())
    val date = inputFormat.parse(orderTime)
    val formattedDate = outputFormat.format(date)
//    println(formattedDate) // prints 10-04-2018

    return formattedDate
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

fun Bitmap.toFile(filePath: String): File? {
    return try {
        val file = File(filePath)
        val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
        compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        file
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun Bitmap?.saveImageToStorage(filename: String) : File {
    val filename = "$filename.jpg"
    var pathfile = ""
    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val image = File(imagesDir, filename)
    pathfile = image.absolutePath
    val fos: OutputStream = FileOutputStream(image)
    fos.use {
        this?.compress(Bitmap.CompressFormat.JPEG, 40, it)
        val fileResult = File(pathfile)

        val b = BitmapFactory.decodeFile(fileResult.absolutePath)

        val origWidth = b.width
        val origHeight = b.height

        if (origWidth > 1080) {
            val destHeight = origHeight / (origWidth / 1080.toFloat())
            val b2 = Bitmap.createScaledBitmap(b, 1080, destHeight.toInt(), false)
            val outStream = ByteArrayOutputStream()
            b2.compress(Bitmap.CompressFormat.JPEG, 40, outStream)
            val fileName = fileResult.absolutePath
            val f = File(fileName)
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(outStream.toByteArray())
            fo.close()
            b.recycle()
            b2.recycle()
            return f
        }

        b.recycle()
        return fileResult
    }
}

fun compressImage(imageFile: File, destWidth: Int = 1080): File  {
    val b = BitmapFactory.decodeFile(imageFile.absolutePath)

    val origWidth = b.width
    val origHeight = b.height

    if (origWidth > destWidth) {
        val destHeight = origHeight / (origWidth / destWidth.toFloat())
        val b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight.toInt(), false)
        val outStream = ByteArrayOutputStream()
        b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream)
        val fileName = imageFile.absolutePath.replace(
            imageFile.name,
            "${imageFile.nameWithoutExtension}-compressed.${imageFile.extension}"
        )
        val f = File(fileName)
        f.createNewFile()
        //write the bytes in file
        val fo = FileOutputStream(f)
        fo.write(outStream.toByteArray())
        // remember close de FileOutput
        fo.close()

        b.recycle()
        b2.recycle()
        return f
    }

    b.recycle()
    return imageFile
}

fun String?.isValidEmail(): Boolean {
    return if (this.isNullOrEmpty()) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}

fun Context.appIsExpired(versionCode: Int) : Boolean {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val appVersion = pInfo.versionCode
        Log.w("appIsExpired", "appVersion: $appVersion | versionCode: $versionCode")
        appVersion < versionCode
    } catch (e: Exception) {
        false
    }
}

fun Context.appIsExpired(versionCode: Long) : Boolean {
    return appIsExpired(versionCode.toInt())
}

fun Context.getAppVersionCode() : Int?{
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionCode
    } catch (e: Exception) {
        0
    }
}

fun Context.getAppVersionName() : String{
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    } catch (e: Exception) {
        "0.0.0"
    }
}

fun Context.getDeviceName(): String {
    return try {
        Build.BRAND + " - " + Build.MODEL
    } catch (e: java.lang.Exception) {
        "Unknown"
    }
}

fun Context.getOSAndroid(): String {
    return if (Build.VERSION.SDK_INT == 34) {
        "Android 14 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 33) {
        "Android 13 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 32) {
        "Android 12 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 31) {
        "Android 12 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 30) {
        "Android 11 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 29) {
        "Android 10 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 28) {
        "Android 9 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 27 || Build.VERSION.SDK_INT == 26) {
        "Android 8 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 25 || Build.VERSION.SDK_INT == 24) {
        "Android 7 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 23) {
        "Android 6 API " + Build.VERSION.SDK_INT
    } else if (Build.VERSION.SDK_INT == 22 || Build.VERSION.SDK_INT == 21) {
        "Android 5 API " + Build.VERSION.SDK_INT
    } else {
        ""
    }
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
            startActivity(Intent.createChooser(intent, "Pilih browser"))
        }
    }
}

fun Int?.isGranted() : Boolean {
    return this == 1
}

fun Activity.gotoAppSetting(){
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}
