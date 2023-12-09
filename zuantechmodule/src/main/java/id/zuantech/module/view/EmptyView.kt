package id.zuantech.module.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import id.zuantech.module.databinding.EmptyViewBinding

class EmptyView : LinearLayout {

    private var mBinding = EmptyViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initComponent()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initComponent()
    }

    private fun initComponent() {
        if(this.isInEditMode){
            isShow(true)
        } else {
            isShow(false)
        }
    }

    fun isShow(iShow: Boolean) {
        if(iShow){
            mBinding.root.visibility = VISIBLE
        } else {
            mBinding.root.visibility = GONE
        }
    }

    fun setDescription(showEmoteIcon: Boolean, description: String) {
        mBinding.ivImageEmoteIcon.visibility = if(showEmoteIcon) VISIBLE else GONE
//        mBinding.tvDescription.text = description
    }

    fun setDescription(description: String) {
//        mBinding.tvDescription.text = description
    }
}