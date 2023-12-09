package id.zuantech.appmodule.view

import android.os.Bundle
import android.os.CountDownTimer
import id.zuantech.appmodule.databinding.SplashScreenActivityBinding
import id.zuantech.appmodule.modules.base.BaseActivity


class SplashScreenActivity : BaseActivity() {

    companion object {
        val TAG = SplashScreenActivity::class.java.simpleName
    }

    lateinit var mBinding: SplashScreenActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = SplashScreenActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        object: CountDownTimer(1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
//                if(repoViewModel.isLoggedIn()){
                    gotoMainActivity()
//                } else {
//                    gotoLoginActivity()
//                }
            }
        }.start()
    }

}