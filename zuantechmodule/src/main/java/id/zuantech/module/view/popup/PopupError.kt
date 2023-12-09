package id.projeku.tvvoucher.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import id.zuantech.module.databinding.PopupErrorBinding

class PopupError(private val context: Context) {

    private val mBinding = PopupErrorBinding.inflate(LayoutInflater.from(context))

    var popupTitle = "Gagal"
    var popupMessage = ""

    fun popupTitle(popupTitle: String): PopupError {
        this.popupTitle = popupTitle
        return this
    }

    fun popupMessage(popupMessage: String): PopupError {
        this.popupMessage = popupMessage
        return this
    }

    var onClickPositive: (() -> Unit)? = null
    fun onClickPositive(onClickPositive: () -> Unit): PopupError {
        this.onClickPositive = onClickPositive
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