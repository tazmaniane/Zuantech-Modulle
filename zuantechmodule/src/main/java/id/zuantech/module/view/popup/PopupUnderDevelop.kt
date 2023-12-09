package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupUnderDevelopBinding

class PopupUnderDevelop(private val context: Context) {

    private val mBinding = PopupUnderDevelopBinding.inflate(LayoutInflater.from(context))

    var popupMessage = ""

    fun popupMessage(popupMessage: String): PopupUnderDevelop {
        this.popupMessage = popupMessage
        return this
    }

    var onClickPositive: (() -> Unit)? = null
    fun onClickPositive(onClickPositive: () -> Unit): PopupUnderDevelop {
        this.onClickPositive = onClickPositive
        return this
    }

    fun onClickPositive(text: String, onClickPositive: () -> Unit): PopupUnderDevelop {
        this.onClickPositive = onClickPositive
        mBinding.btnPositive.text = text
        return this
    }

    fun show() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(mBinding.root)
        val alertDialog = alertDialogBuilder.create()

        mBinding.apply {
            if(!popupMessage.isNullOrEmpty()) tvMessage.text = popupMessage

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