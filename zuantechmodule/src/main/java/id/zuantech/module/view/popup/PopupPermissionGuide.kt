package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupPermissionGuideBinding

class PopupPermissionGuide(private val context: Context) {

    private val mBinding = PopupPermissionGuideBinding.inflate(LayoutInflater.from(context))

    var isShowing = false
    var popupTitle = "Izin Diperlukan"
    var popupMessage = ""
    var positiveText = "Oke"

    fun popupTitle(popupTitle: String): PopupPermissionGuide {
        this.popupTitle = popupTitle
        return this
    }

    fun popupMessage(popupMessage: String): PopupPermissionGuide {
        this.popupMessage = popupMessage
        return this
    }

    var onClickPositive: (() -> Unit)? = null
    fun onClickPositive(onClickPositive: () -> Unit): PopupPermissionGuide {
        this.onClickPositive = onClickPositive
        return this
    }

    fun onClickPositive(positiveText: String, onClickPositive: () -> Unit): PopupPermissionGuide {
        this.positiveText = positiveText
        this.onClickPositive = onClickPositive
        this.mBinding.btnPositive.text = positiveText
        return this
    }

    var alertDialogBuilder: AlertDialog.Builder? = null
    var alertDialog: AlertDialog? = null

    init {
        alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder?.setView(mBinding.root)
        alertDialog = alertDialogBuilder?.create()

        mBinding.apply {
            btnPositive.text = positiveText
            btnPositive.setOnClickListener {
                onClickPositive?.let { it1 -> it1() }
                isShowing = false
                alertDialogBuilder?.setView(null)
                alertDialog?.dismiss()
            }
        }

        alertDialog?.setCanceledOnTouchOutside(true)
        alertDialog?.setCancelable(true)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        mBinding.tvTitle.text = popupTitle
        mBinding.tvMessage.text = popupMessage
        alertDialog?.show()
        isShowing = true
    }

}