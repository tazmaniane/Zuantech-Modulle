package id.zuantech.appmodule.view.main

import android.os.Bundle
import id.zuantech.appmodule.databinding.MainActivityBinding
import id.zuantech.appmodule.modules.base.BaseActivity

class MainActivity : BaseActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private val mBinding by lazy { MainActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        initView()
    }

    private fun initView() {

    }

}