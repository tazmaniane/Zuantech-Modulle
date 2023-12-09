package id.zuantech.module.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.core.view.isVisible
import id.zuantech.module.databinding.LoadingViewBinding

class LoadingView : LinearLayout {

    private var mBinding = LoadingViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initComponent()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initComponent()
    }

    private fun initComponent() {
        isShow(false)
    }

    var isShow = false

    fun isShow(isShow: Boolean) {
        this.isShow = isShow
        mBinding.progress.isVisible = this.isShow
        if(this.isShow){
            this.visibility = VISIBLE
        } else {
            this.visibility = GONE
        }
    }

    fun setDescription(text: String) {
        if(text.isNotEmpty()){
            mBinding.tvDescription.visibility = VISIBLE
            mBinding.tvDescription.text = text
        } else {
            mBinding.tvDescription.visibility = GONE
        }
    }
}