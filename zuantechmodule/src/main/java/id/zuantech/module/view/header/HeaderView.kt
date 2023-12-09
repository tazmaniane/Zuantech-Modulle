package id.zuantech.module.view.header

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import id.zuantech.module.R
import id.zuantech.module.databinding.HeaderViewBinding

class HeaderView : FrameLayout {

    companion object {
        val TAG = HeaderView::class.java.simpleName
    }

    private var mBinding = HeaderViewBinding.inflate(LayoutInflater.from(context), this, true)
    private var preferences: SharedPreferences? = null
    private var headerUserViewListener: HeaderUserViewListener? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initComponent()
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initComponent()
    }

    interface HeaderUserViewListener {
        fun onclickback()
    }

    fun setHeaderUserViewListener(headerUserViewListener: HeaderUserViewListener): HeaderView {
        this.headerUserViewListener = headerUserViewListener
        return this
    }

    fun setTitle(title: String?): HeaderView {
        mBinding.lineHeaderTitle.visibility = GONE
        title?.let {
            mBinding.tvHeaderTitle.text = it
            mBinding.lineHeaderTitle.visibility = VISIBLE
        }
        return this
    }

    fun setTitle(title: String?, isTransparent: Boolean): HeaderView {
        mBinding.lineHeaderTitle.visibility = GONE
        title?.let {
            mBinding.tvHeaderTitle.text = it
            mBinding.lineHeaderTitle.visibility = VISIBLE
        }
        if(isTransparent){
            mBinding.layoutRoot.background = ContextCompat.getDrawable(context, R.drawable.bg_transclude_black)
        } else {
            mBinding.layoutRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        return this
    }

//    fun setTitleIcon(drawable: Drawable?): HeaderUserViewV3 {
//        mBinding.ivIcon.setImageDrawable(drawable)
//        return this
//    }
//
//    fun setTitleIcon(drawable: Drawable?, tintColor: Int): HeaderUserViewV3 {
//        mBinding.ivIcon.setImageDrawable(drawable)
//        mBinding.ivIcon.setColorFilter(ContextCompat.getColor(context, tintColor))
//        return this
//    }

    fun isShowBackButton(isShowBackButton: Boolean): HeaderView {
        mBinding.btnBack.visibility = if(isShowBackButton) VISIBLE else GONE
        return this
    }

    fun isShowBackButton(isShowBackButton: Boolean, onClick: () -> Unit): HeaderView {
        mBinding.btnBack.visibility = if(isShowBackButton) VISIBLE else GONE
        if(isShowBackButton){
            mBinding.btnBack.setOnClickListener { onClick() }
        }
        return this
    }

//    fun setActionContent(drawable: Drawable?): HeaderUserViewV3 {
//        mBinding.lineAction.visibility = GONE
//        drawable.let {
//            mBinding.ivAction.setImageDrawable(it)
//            mBinding.lineAction.visibility = VISIBLE
//        }
//        return this
//    }

    fun setAction1Content(title: String?, icon: Int?, onClick: () -> Unit): HeaderView {
        mBinding.btnAction1.visibility = VISIBLE
        mBinding.btnAction1.text = title ?: ""
        icon?.let {
            mBinding.btnAction1.icon = ContextCompat.getDrawable(context, it)
        }
        mBinding.btnAction1.setOnClickListener {
            onClick()
        }
        return this
    }

    fun setActionDeleteContent(onClick: () -> Unit): HeaderView {
        mBinding.btnAction2.visibility = VISIBLE
        mBinding.btnAction2.icon = ContextCompat.getDrawable(context, R.drawable.ic_trash)
        mBinding.btnAction2.setOnClickListener {
            onClick()
        }
        return this
    }

//    fun setBadgeCount(count: Int): HeaderUserViewV3 {
//        mBinding.lineAction.visibility = VISIBLE
//        mBinding.lineBadge.visibility = GONE
//        if(count > 0){
//            mBinding.tvBadge.text = if(count > 9) "9+" else count.toString()
//            mBinding.lineBadge.visibility = VISIBLE
//        }
//        return this
//    }


    private fun initComponent(){
        if (!this.isInEditMode) {
            mBinding.btnAction1.visibility = GONE
            mBinding.btnBack.setOnClickListener {
                headerUserViewListener?.onclickback()
            }
        }
    }
}