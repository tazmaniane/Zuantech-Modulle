package id.projeku.tvvoucher.view.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import id.zuantech.module.databinding.PopupFloatingMessageBinding
import id.zuantech.module.ext.hideKeyboard

class PopupFloatingMessage(private val context: Context) {

    private val mBinding = PopupFloatingMessageBinding.inflate(LayoutInflater.from(context))

    var isShowing = false
    var popupMessage = ""
    private var mPopupWindow: PopupWindow? = null

    fun popupMessage(popupMessage: String): PopupFloatingMessage {
        this.popupMessage = popupMessage
        return this
    }

    init {
        mBinding.apply {
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true
            mPopupWindow = PopupWindow(root, width, height, focusable)
            root.setOnFocusChangeListener { v, hasFocus ->
                if(!hasFocus) {
                    mPopupWindow?.dismiss()
                }
            }
        }
    }

    fun show(view: View) {
        mBinding.tvMessage.text = popupMessage
        mPopupWindow?.let { popup ->
            if (popup.isShowing) {
                popup.dismiss()
            } else {
                popup.showAsDropDown(view, Gravity.BOTTOM, 0, 0)
            }
            context.hideKeyboard(view)
        }
        isShowing = true
    }

}