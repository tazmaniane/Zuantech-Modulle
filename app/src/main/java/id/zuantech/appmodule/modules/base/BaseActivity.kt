package id.zuantech.appmodule.modules.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import id.zuantech.appmodule.R
import id.zuantech.appmodule.view.main.MainActivity
import id.zuantech.appmodule.viewmodel.RepoViewModel

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    val repoViewModel by viewModels<RepoViewModel>()

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            window.navigationBarColor = ContextCompat.getColor(this, R.color.grey_lighter)
//            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
        }
        return super.onCreateView(name, context, attrs)
    }

    override fun onBackPressed() {
        onClickBack()
    }

    open fun setStatusBarColor(color: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = ContextCompat.getColor(this, color)
        }
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

    open fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    open fun gotoLoginActivity() {
//        repoViewModel.logout()
//        getSharedPreferences("session", Context.MODE_PRIVATE).edit().clear().apply()
//        startActivity(Intent(this, LoginActivity::class.java))
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finishAffinity()
    }
}