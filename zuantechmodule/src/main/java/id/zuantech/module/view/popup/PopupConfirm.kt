package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupConfirmBinding

class PopupConfirm(private val context: Context) {

    private val mBinding = PopupConfirmBinding.inflate(LayoutInflater.from(context))

    var isShowing = false
    var popupTitle = "Konfirmasi"
    var popupMessage = ""
    var positiveText = "Lanjut"

    fun popupTitle(popupTitle: String): PopupConfirm {
        this.popupTitle = popupTitle
        return this
    }

    fun popupMessage(popupMessage: String): PopupConfirm {
        this.popupMessage = popupMessage
        return this
    }

    var onClickPositive: (() -> Unit)? = null
    fun onClickPositive(onClickPositive: () -> Unit): PopupConfirm {
        this.onClickPositive = onClickPositive
        return this
    }

    fun onClickPositive(positiveText: String, onClickPositive: () -> Unit): PopupConfirm {
        this.positiveText = positiveText
        this.onClickPositive = onClickPositive
        this.mBinding.btnPositive.text = positiveText
        return this
    }
    
    var onClickNegative: (() -> Unit)? = null
    fun onClickNegative(onClickNegative: () -> Unit): PopupConfirm {
        this.onClickNegative = onClickNegative
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

            btnNegative.setOnClickListener {
                onClickNegative?.let { it1 -> it1() }
                isShowing = false
                alertDialogBuilder?.setView(null)
                alertDialog?.dismiss()
            }
        }

        alertDialog?.setCanceledOnTouchOutside(false)
        alertDialog?.setCancelable(false)
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        mBinding.tvTitle.text = popupTitle
        mBinding.tvMessage.text = popupMessage
        alertDialog?.show()
        isShowing = true
    }

}