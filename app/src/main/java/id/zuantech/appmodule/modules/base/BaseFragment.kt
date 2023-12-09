package id.zuantech.appmodule.modules.base

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.zuantech.appmodule.R
import id.zuantech.appmodule.view.main.MainActivity
import id.zuantech.appmodule.viewmodel.RepoViewModel

@AndroidEntryPoint
abstract  class BaseFragment() : Fragment() {

    val repoViewModel by viewModels<RepoViewModel>()
    val preference by lazy(LazyThreadSafetyMode.NONE) { requireContext().getSharedPreferences("session", AppCompatActivity.MODE_PRIVATE) }

    open fun setStatusBarColor(color: Int){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), color)
        }
    }

    open fun gotoLoginActivity() {
//        repoViewModel.logout()
//        preference.edit().clear().apply()
//        requireContext().startActivity(Intent(requireContext(), LoginActivity::class.java))
//        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        requireActivity().finishAffinity()
    }

    open fun startActivityNormal(intent: Intent) {
        requireContext().startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    open fun startActivityNormalForResult(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    open fun gotoMainActivity() {
        requireContext().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        requireActivity().finish()
    }
}