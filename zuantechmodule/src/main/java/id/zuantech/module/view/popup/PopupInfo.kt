package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupInfoBinding

class PopupInfo(private val context: Context) {

    private val mBinding = PopupInfoBinding.inflate(LayoutInflater.from(context))

    var popupTitle = "Informasi"
    var popupMessage = ""

    fun popupTitle(popupTitle: String): PopupInfo {
        this.popupTitle = popupTitle
        return this
    }

    fun popupMessage(popupMessage: String): PopupInfo {
        this.popupMessage = popupMessage
        return this
    }

    var onClickPositive: (() -> Unit)? = null
    fun onClickPositive(onClickPositive: () -> Unit): PopupInfo {
        this.onClickPositive = onClickPositive
        return this
    }

    fun onClickPositive(text: String, onClickPositive: () -> Unit): PopupInfo {
        this.onClickPositive = onClickPositive
        mBinding.btnPositive.text = text
        return this
    }

    fun show() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(mBinding.root)
        val alertDialog = alertDialogBuilder.create()

        mBinding.apply {
            tvTitle.text = popupTitle
            tvMessage.text = popupMessage

            btnPositive.setOnClickListener {
                onClickPositive?.let { it1 -> it1() }
                alertDialog.dismiss()
            }
        }

        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}