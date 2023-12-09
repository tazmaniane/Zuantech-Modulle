package id.zuantech.module.base

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import id.zuantech.module.R

abstract class BaseActivity() : AppCompatActivity() {

    val animation by lazy { AnimationUtils.loadAnimation(this , R.anim.shake) }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            window.navigationBarColor = ContextCompat.getColor(this, R.color.grey_lighter)
//            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
        }
        return super.onCreateView(name, context, attrs)
    }

    open fun onClickBack() {
        finishAfterTransition()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    open fun startActivityNormal(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    open fun startActivityNormalForResult(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    open fun gotoAppSetting(){
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}